import { Injectable } from '@angular/core';
import { MatPaginatorIntl } from '@angular/material/paginator';

// The Item list shows a refresh icon where "Items per page:" normally appears, so that label is left empty
@Injectable()
export class ItemPaginatorIntl extends MatPaginatorIntl {
  override itemsPerPageLabel = '';
}
