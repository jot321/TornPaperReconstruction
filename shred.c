#include <stdio.h>
#include <stdlib.h>

typedef struct _edges{
	int size;
	int *pixels;
	double probFirst,probLast;
}edges;

typedef struct _listEdges{
	edges *current;
	struct _listEdges *next;
}listEdges;

typedef struct _shreds{
	edges *top,*down,*right,*left;
}shreds;

typedef struct _setShreds{
	shreds *current;
	struct _setShreds *next;
}setShreds;

typedef struct _neighbourEdges{
	edges *EX;
	edges *EY;
}neighbourEdges;

typedef struct _listNeighbours{
	neighbourEdges *current;
	struct _listNeighbours *next;
}listNeighbours;

/*
typedef struct _setShreds{
	listShreds *entities;
	listNeighbours *neighbours;
}setShreds;
*/
typedef struct _listSetShreds{
	setShreds *current;
	struct _listSetShreds *next;
}listSetShreds;

typedef struct _listEdgesProbs{
	edges *E;
	listEdges *list;
	struct _listEdgesProb *next;
}listEdgesProb;

int isPresentShred(setShreds *list,shreds *S)
{
	if(list == NULL || S == NULL)
		return 0;	
	while(list != NULL)
	{
		if(list->current == S)
			return 1;
		list = list->next;
	}
	return 0;
}

setShreds *Union(setShreds *X,setShreds *Y)
{
	setShreds *temp,*head=NULL,*current=NULL;
	
	while(X != NULL)
	{
		if(isPresentShred(head,X->current)==0)
		{
			temp = (setShreds *)malloc(sizeof(setShreds));
			temp->current = X->current;
			temp->next = NULL;
			if(head==NULL)
				head = current = temp;
			else
			{
				current->next = temp;
				current = current->next;
			}
		}
		X = X->next;
	}
	while(Y != NULL)
	{
		if(isPresentShred(head,Y->current)!=0)
		{
			temp = (setShreds *)malloc(sizeof(setShreds));
			temp->current = Y->current;
			temp->next = NULL;
			if(head==NULL)
				head = currrent = temp;
			else
			{
				current->next = temp;
				current = current->next;
			}
		}
		Y = Y->next;
	}
	return head;
}

setShreds *GetSet(edges *E,listSetShreds *list)
{
	setShreds *S;
	shreds *shred;
	if(E == NULL || list == NULL)
		return NULL;
	while(list != NULL)
	{
		S = list->current;
		while(S != NULL)
		{
			shred = S->current;
			if(shred != NULL)
			{
				if(shred->top == E || shred->down == E || shred->left == E || shred->right == E)
					return S;
			}
			S = S->next;
		}
		list = list->next;
	}
	return NULL;
}

int isNeighbour(edges *X,edges *Y,listNeighbours *list)
{
	neighbourEdges *neighbours;
	while(list != NULL)
	{
		neighbours = list->current;
		if(neighbours != NULL)
		{
			if(neighbours->EX == X && neighbours->EY == Y)
				return 1;
		}
		list = list->next;
	}
	return 0;
}

double GetProb(edges *EX,edges *EY,listSetShreds *listsets)
{
	double prob = 1.0;
	setShreds *SX = GetSet(EX,listsets), *SY = GetSet(EY,listsets);
	setShreds *merged = Union(SX, SY),*Sx=SX,*Sy=SY;
	shreds *tempSx,*tempSy;
	
	while(Sx != NULL)
	{
		Sy = SY;
		while(Sy != NULL)
		{
			tempSx = Sx->current;
			tempSy = Sy->current;
			if(isNeighbour(tempSx->top,tempSy->top))
				prob *= Pr(tempSx->top,tempSy->top);
			if(isNeighbour(tempSy->top,tempSx->top))
				prob *= Pr(tempSy->top,tempSx->top);
				
			if(isNeighbour(tempSx->top,tempSy->down))
				prob *= Pr(tempSx->top,tempSy->down);
			if(isNeighbour(tempSy->top,tempSx->down))
				prob *= Pr(tempSy->top,tempSx->down);
			
			if(isNeighbour(tempSx->top,tempSy->right))
				prob *= Pr(tempSx->top,tempSy->right);
			if(isNeighbour(tempSy->top,tempSx->right))
				prob *= Pr(tempSy->top,tempSx->right);
				
			if(isNeighbour(tempSx->top,tempSy->left))
				prob *= Pr(tempSx->top,tempSy->left);
			if(isNeighbour(tempSy->top,tempSx->left))
				prob *= Pr(tempSy->top,tempSx->left);
				
			if(isNeighbour(tempSx->down,tempSy->top))
				prob *= Pr(tempSx->down,tempSy->top);
			if(isNeighbour(tempSy->down,tempSx->top))
				prob *= Pr(tempSy->down,tempSx->top);
				
			if(isNeighbour(tempSx->down,tempSy->down))
				prob *= Pr(tempSx->down,tempSy->down);
			if(isNeighbour(tempSy->down,tempSx->down))
				prob *= Pr(tempSy->down,tempSx->down);
			
			if(isNeighbour(tempSx->down,tempSy->right))
				prob *= Pr(tempSx->down,tempSy->right);
			if(isNeighbour(tempSy->down,tempSx->right))
				prob *= Pr(tempSy->down,tempSx->right);
				
			if(isNeighbour(tempSx->down,tempSy->left))
				prob *= Pr(tempSx->down,tempSy->left);
			if(isNeighbour(tempSy->down,tempSx->left))
				prob *= Pr(tempSy->down,tempSx->left);
				
			if(isNeighbour(tempSx->right,tempSy->top))
				prob *= Pr(tempSx->right,tempSy->top);
			if(isNeighbour(tempSy->right,tempSx->top))
				prob *= Pr(tempSy->right,tempSx->top);
				
			if(isNeighbour(tempSx->right,tempSy->down))
				prob *= Pr(tempSx->right,tempSy->down);
			if(isNeighbour(tempSy->right,tempSx->down))
				prob *= Pr(tempSy->right,tempSx->down);
			
			if(isNeighbour(tempSx->right,tempSy->right))
				prob *= Pr(tempSx->right,tempSy->right);
			if(isNeighbour(tempSy->right,tempSx->right))
				prob *= Pr(tempSy->right,tempSx->right);
				
			if(isNeighbour(tempSx->right,tempSy->left))
				prob *= Pr(tempSx->right,tempSy->left);
			if(isNeighbour(tempSy->right,tempSx->left))
				prob *= Pr(tempSy->right,tempSx->left);
				
			if(isNeighbour(tempSx->left,tempSy->top))
				prob *= Pr(tempSx->left,tempSy->top);
			if(isNeighbour(tempSy->left,tempSx->top))
				prob *= Pr(tempSy->left,tempSx->top);
				
			if(isNeighbour(tempSx->left,tempSy->down))
				prob *= Pr(tempSx->left,tempSy->down);
			if(isNeighbour(tempSy->left,tempSx->down))
				prob *= Pr(tempSy->left,tempSx->down);
			
			if(isNeighbour(tempSx->left,tempSy->right))
				prob *= Pr(tempSx->left,tempSy->right);
			if(isNeighbour(tempSy->left,tempSx->right))
				prob *= Pr(tempSy->left,tempSx->right);
				
			if(isNeighbour(tempSx->left,tempSy->left))
				prob *= Pr(tempSx->left,tempSy->left);
			if(isNeighbour(tempSy->left,tempSx->left))
				prob *= Pr(tempSy->left,tempSx->left);
			
			
			Sy = Sy->next;
		}
		Sx = Sx->next;
	}
	return prob;
}

void normProb(double *probs,int nEdges,listEdges *list,edges *E,listSetShreds *listsets)
{
	double prob=1.0,norm=0.0;
	listEdges *tempList = list;
	edges *temp;
	while(tempList != NULL)
	{
		temp = tempList->current;
		norm += GetProb(E,temp,listsets);
		tempList = tempList->next;
	}
	tempList = list;
	int i=0;
	while(tempList != NULL && i< nEdges)
	{
		temp = tempList->current;
		probs[i++] = GetProb(E,temp,listsets) / norm;
		tempList = tempList->next;
	}
}

void normalize(double **probs,int nEdges,listEdges *list,listSetShreds *listsets)
{
	listEdges *tempList = list;
	for(i=0;i<nEdges && tempList != NULL;i++)
	{
		normProb(probs[i],nEdges,list,tempList->currrent,listsets);
		tempList = tempList->next;
	}
}
