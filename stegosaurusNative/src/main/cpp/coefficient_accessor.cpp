#include "coefficient_accessor.h"
/* For memcpy */
#include <string.h>
#include <assert.h>

CoefficientAccessor::CoefficientAccessor(JPEGComponent **componentArray,
                                         int total)
    : components(NULL),
      totalComponents(total),
      length(-1),
      usables(NULL) {
  components = new JPEGComponent*[total];
  memcpy(components, componentArray, sizeof(JPEGComponent *) * total);
}

CoefficientAccessor::~CoefficientAccessor(void) {
  delete [] components;
  if(usables) {
    free(usables);
  }
}

JPEGComponent* CoefficientAccessor::findComponent(unsigned int *index) {
  unsigned int c = 0;
  JPEGComponent *comp = components[0];
  while((c < totalComponents) &&
        (*index) >= comp->getTotalNumberOfCoefficients()) {
    (*index) -= comp->getTotalNumberOfCoefficients();
    c++;
    comp = components[c];
  }
  return comp;
}

JCOEF* CoefficientAccessor::getInComponent(unsigned int index,
                                           JPEGComponent *comp) {
  int row, row_local, col, coef_index;
  const int block_size = comp->getBlockSize();
  const int width = comp->getWidthInBlocks();
  row_local = index % (width * block_size);
  row = index / (width * block_size);
  col = row_local / block_size;
  coef_index = row_local % block_size;
  return &(comp->getCoefficients()[row][col][coef_index]);
}

JCOEF CoefficientAccessor::getCoefficient(unsigned int index) {
  JPEGComponent *comp = findComponent(&index);
  return *(getInComponent(index, comp));
}

void CoefficientAccessor::setCoefficient(unsigned int index, JCOEF value) {
  JPEGComponent *comp = findComponent(&index);
  *(getInComponent(index, comp)) = value;
}

unsigned int CoefficientAccessor::getLength(void) {
  if(length > 0) {
    return length;
  }
  unsigned int c;
  length = 0;
  for(c = 0; c < totalComponents; c++) {
    length += (components[c])->getTotalNumberOfCoefficients();
  }
  return length;
}

bool CoefficientAccessor::isDC(unsigned int index) {
  int size;
  JPEGComponent *comp = findComponent(&index);
  size = comp->getBlockSize();
  return ((index % size) == 0);
}

int* CoefficientAccessor::getUsableCoefficients(void) {
  if(usables) {
    return usables;
  }
  usables = (int *) malloc(sizeof(int) * getLength());
  int i;
  int j = 0;
  /* length has been set for sure now, since we just called getLength() */
  for(i = 0; i < length; ++i) {
    /* We get a new variable, so as to not mess with i */
    unsigned int index = i;
    JPEGComponent *comp = findComponent(&index);
    if((index % comp->getBlockSize()) && (*getInComponent(index, comp))) {
      usables[j] = i;
      ++j;
    }
  }
  usables = (int *) realloc(usables, sizeof(int) * j);
  usableCount = j;
  return usables;
}

int CoefficientAccessor::getUsableCoefficientCount(void) {
  /* Ensure we've actually loaded the count. */
  if(!usables) {
    getUsableCoefficients();
  }
  return usableCount;
}

void CoefficientAccessor::cannibalizeUsables(CoefficientAccessor *other) {
  usableCount = other->getUsableCoefficientCount();
  usables = (int *) malloc(sizeof(int) * usableCount);
  usables = (int *) memcpy(usables, other->getUsableCoefficients(),
    sizeof(int) * usableCount);
}
