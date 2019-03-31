import {Component, Input, OnChanges} from '@angular/core';

@Component({
  selector: 'app-palette-displayer',
  templateUrl: './palette-displayer.component.html',
  styleUrls: ['./palette-displayer.component.css']
})
export class PaletteDisplayerComponent implements OnChanges {

  @Input()
  paletteString: string;

  palettes: string[] = [];

  constructor() { }

  ngOnChanges() {
    this.palettes.length = 0;
    if (this.paletteString) {
      this.paletteString.split(";").filter(palette => !!palette && ";" !== palette).forEach(palette => this.palettes[this.palettes.length] = palette);
    }
  }
}
