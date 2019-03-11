import {Component, OnInit} from '@angular/core';
import {HelloWorldService} from "./services/hello-world.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'app';
  helloWorld: string = "(No Response Yet)"
  constructor(private helloWorldService: HelloWorldService) {

  }

  public ngOnInit(): void {
    this.helloWorldService.helloWorld().subscribe(response => {
      console.log(response);
      this.helloWorld = response;
    })
  }

}
