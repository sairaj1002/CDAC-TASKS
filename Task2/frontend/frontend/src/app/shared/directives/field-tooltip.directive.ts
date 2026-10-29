import { Directive, ElementRef, HostListener, Input, OnDestroy, OnInit } from '@angular/core';

@Directive({
  selector: 'input:not([type="button"]):not([type="submit"]):not([type="reset"]):not([type="checkbox"]):not([type="radio"]):not([type="file"]), select, textarea, [appFieldTooltip]',
  standalone: false
})
export class FieldTooltipDirective implements OnInit, OnDestroy {

  @Input('appFieldTooltip') label: string = '';
  @Input() tooltipValue: any = null;

  private tooltipEl: HTMLElement | null = null;
  private arrowEl: HTMLElement | null = null;

  constructor(private elementRef: ElementRef<HTMLElement>) {}

  ngOnInit(): void {}

  @HostListener('mouseenter')
  onMouseEnter(): void {
    this.showTooltip();
  }

  @HostListener('mouseleave')
  onMouseLeave(): void {
    this.hideTooltip();
  }

  @HostListener('focusin')
  onFocusIn(): void {
    this.showTooltip();
  }

  @HostListener('focusout')
  onFocusOut(): void {
    this.hideTooltip();
  }

  @HostListener('input')
  onInput(): void {
    this.updateContent();
  }

  @HostListener('change')
  onChange(): void {
    this.updateContent();
  }

  @HostListener('window:scroll')
  onWindowScroll(): void {
    if (this.tooltipEl) {
      this.positionTooltip();
    }
  }

  private cleanLabelText(raw: string): string {
    if (!raw) return '';
    return raw
      .replace(/\([^\)]*\)/g, '')      // remove anything in parentheses like "(cannot be changed after saving)"
      .replace(/\[[^\]]*\]/g, '')      // remove anything in square brackets
      .replace(/\d+\s*\/\s*\d+/g, '')  // remove counts like "10/50"
      .replace(/[\*\:\#]/g, '')        // remove asterisks, colons, hashes
      .replace(/\s+/g, ' ')            // normalize whitespace
      .trim();
  }

  private resolveLabel(): string {
    if (this.label && this.label.trim()) {
      return this.cleanLabelText(this.label);
    }

    const host = this.elementRef.nativeElement;

    // Check data-field-label attribute
    const dataLabel = host.getAttribute('data-field-label');
    if (dataLabel) {
      return this.cleanLabelText(dataLabel);
    }

    // Try finding label in closest container (.wamis-wms-field, .wamis-form-group, etc.)
    const parent = host.closest('.wamis-wms-field, .wamis-form-group, .wamis-custom-select-container, .wamis-form-field, .wamis-input-wrapper, td, .form-group');
    if (parent) {
      const labelEl = parent.querySelector('label, .wamis-wms-label, .wamis-form-label, .wamis-custom-select-label');
      if (labelEl) {
        const clone = labelEl.cloneNode(true) as HTMLElement;
        const toRemove = clone.querySelectorAll('.wamis-field-desc, .wamis-char-count, small, .wamis-required-star, .text-muted, .text-danger, .required, span.text-danger');
        toRemove.forEach(el => el.remove());
        const text = this.cleanLabelText(clone.textContent || '');
        if (text) return text;
      }
    }

    // Check preceding element sibling if it's a label
    let prev = host.previousElementSibling;
    while (prev) {
      if (prev.tagName === 'LABEL' || prev.classList.contains('wamis-form-label') || prev.classList.contains('wamis-wms-label')) {
        const clone = prev.cloneNode(true) as HTMLElement;
        const toRemove = clone.querySelectorAll('.wamis-field-desc, .wamis-char-count, small, .wamis-required-star, .text-muted, .text-danger, .required');
        toRemove.forEach(el => el.remove());
        const text = this.cleanLabelText(clone.textContent || '');
        if (text) return text;
      }
      prev = prev.previousElementSibling;
    }

    // Check placeholder
    const placeholder = host.getAttribute('placeholder');
    if (placeholder) {
      let pText = placeholder.replace(/^(enter|select|input)\s+/i, '').trim();
      pText = this.cleanLabelText(pText);
      if (pText) return pText;
    }

    // Check name, id, or formControlName
    const nameOrId = host.getAttribute('formControlName') || host.getAttribute('name') || host.getAttribute('id');
    if (nameOrId) {
      const formatted = nameOrId
        .replace(/[-_]/g, ' ')
        .replace(/([a-z])([A-Z])/g, '$1 $2')
        .replace(/\b\w/g, l => l.toUpperCase())
        .trim();
      if (formatted) return formatted;
    }

    return '';
  }

  private resolveValue(): string {
    if (this.tooltipValue !== null && this.tooltipValue !== undefined && this.tooltipValue !== '') {
      return String(this.tooltipValue);
    }

    const host = this.elementRef.nativeElement as any;

    if (host.tagName === 'SELECT') {
      const sel = host as HTMLSelectElement;
      if (sel.selectedIndex >= 0 && sel.options[sel.selectedIndex]) {
        const text = sel.options[sel.selectedIndex].text.trim();
        if (!text.toLowerCase().startsWith('select') && !text.toLowerCase().startsWith('--')) {
          return text;
        }
      }
      return '';
    }

    if (host.value !== undefined && host.value !== null) {
      return String(host.value).trim();
    }

    return '';
  }

  private showTooltip(): void {
    const heading = this.resolveLabel();
    if (!heading) return;

    this.hideTooltip();

    const value = this.resolveValue();

    this.tooltipEl = document.createElement('div');
    this.tooltipEl.className = 'wamis-field-tooltip-box';

    const headerEl = document.createElement('div');
    headerEl.className = 'wamis-field-tooltip-header';
    headerEl.textContent = heading;

    const bodyEl = document.createElement('div');
    bodyEl.className = 'wamis-field-tooltip-body';
    bodyEl.textContent = value;

    this.arrowEl = document.createElement('div');
    this.arrowEl.className = 'wamis-field-tooltip-arrow';

    this.tooltipEl.appendChild(headerEl);
    this.tooltipEl.appendChild(bodyEl);
    this.tooltipEl.appendChild(this.arrowEl);

    document.body.appendChild(this.tooltipEl);

    this.positionTooltip();
  }

  private updateContent(): void {
    if (!this.tooltipEl) return;
    const bodyEl = this.tooltipEl.querySelector('.wamis-field-tooltip-body');
    if (bodyEl) {
      bodyEl.textContent = this.resolveValue();
    }
    this.positionTooltip();
  }

  private positionTooltip(): void {
    if (!this.tooltipEl) return;

    const hostRect = this.elementRef.nativeElement.getBoundingClientRect();
    const tooltipRect = this.tooltipEl.getBoundingClientRect();

    let left = hostRect.left + (hostRect.width / 2) - (tooltipRect.width / 2) + window.scrollX;
    let top = hostRect.top - tooltipRect.height - 8 + window.scrollY;

    // Viewport boundaries
    if (left < 10) left = 10;
    if (left + tooltipRect.width > window.innerWidth - 10) {
      left = window.innerWidth - tooltipRect.width - 10;
    }

    if (top < window.scrollY + 10) {
      // Flip below host element
      top = hostRect.bottom + 8 + window.scrollY;
      this.tooltipEl.classList.add('wamis-tooltip-below');
    } else {
      this.tooltipEl.classList.remove('wamis-tooltip-below');
    }

    this.tooltipEl.style.top = `${top}px`;
    this.tooltipEl.style.left = `${left}px`;

    // Position arrow directly over center of host
    if (this.arrowEl) {
      let arrowLeft = (hostRect.left + hostRect.width / 2 + window.scrollX) - left;
      if (arrowLeft < 14) arrowLeft = 14;
      if (arrowLeft > tooltipRect.width - 14) arrowLeft = tooltipRect.width - 14;
      this.arrowEl.style.left = `${arrowLeft}px`;
    }
  }

  private hideTooltip(): void {
    if (this.tooltipEl && this.tooltipEl.parentNode) {
      this.tooltipEl.parentNode.removeChild(this.tooltipEl);
    }
    this.tooltipEl = null;
    this.arrowEl = null;
  }

  ngOnDestroy(): void {
    this.hideTooltip();
  }
}
