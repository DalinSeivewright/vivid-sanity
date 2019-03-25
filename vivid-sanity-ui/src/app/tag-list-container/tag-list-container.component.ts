import {Component, Input, OnInit} from '@angular/core';
import {TagInfoModel} from "../model/tag-info.model";

@Component({
  selector: 'app-tag-list-container',
  templateUrl: './tag-list-container.component.html',
  styleUrls: ['./tag-list-container.component.css']
})
export class TagListContainerComponent implements OnInit {
  @Input()
  tags: TagInfoModel[] = []
  constructor() { }

  ngOnInit() {
  }
}
