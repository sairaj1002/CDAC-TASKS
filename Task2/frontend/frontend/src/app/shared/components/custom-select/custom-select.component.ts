import { Component, ElementRef, EventEmitter, HostListener, Input, Output } from '@angular/core';

@Component({
  selector: 'app-custom-select',
  standalone: false,
  templateUrl: './custom-select.component.html'
})
export class CustomSelectComponent {

  @Input() label: string = '';
  @Input() required: boolean = false;
  @Input() placeholder: string = 'Select option';
  @Input() options: string[] = [];
  @Input() value: string = '';
  @Input() tooltipText: string = '';

  @Output() valueChange = new EventEmitter<string>();

  isOpen = false;
  hoveredIndex = -1;

  constructor(private elementRef: ElementRef) {}

  toggleDropdown(event: Event): void {
    event.stopPropagation();
    this.isOpen = !this.isOpen;
    if (this.isOpen && this.value) {
      this.hoveredIndex = this.options.indexOf(this.value);
    }
  }

  selectOption(opt: string, event: Event): void {
    event.stopPropagation();
    this.value = opt;
    this.valueChange.emit(this.value);
    this.isOpen = false;
  }

  clearValue(event: Event): void {
    event.stopPropagation();
    this.value = '';
    this.valueChange.emit('');
    this.isOpen = false;
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: Event): void {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.isOpen = false;
    }
  }
}
