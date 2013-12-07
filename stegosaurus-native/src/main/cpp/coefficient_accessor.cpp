#include "coefficient_accessor.h"
/* For memcpy */
#include <string.h>

CoefficientAccessor::CoefficientAccessor(JPEGComponent **componentArray,
                                         int total)
    : components(NULL),
      totalComponents(total) {
  components = new JPEGComponent*[total];
  memcpy(components, componentArray, sizeof(JPEGComponent *) * total);
}

CoefficientAccessor::~CoefficientAccessor(void) {
  delete [] components;
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
  const int block_size = comp->getBlockHeight() * comp->getBlockWidth();
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

int CoefficientAccessor::getLength(void) {
  int retval = 0;
  unsigned int c;
  for(c = 0; c < totalComponents; c++) {
    retval += (components[c])->getTotalNumberOfCoefficients();
  }
  return retval;
}

bool CoefficientAccessor::isDC(unsigned int index) {
  int size;
  JPEGComponent *comp = findComponent(&index);
  size = comp->getBlockWidth() * comp->getBlockHeight();
  return ((index % size) == 0);
}
