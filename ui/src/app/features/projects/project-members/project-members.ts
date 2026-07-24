import { Component, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { UserModel } from '../../../core/models/user.model';
import { PageResponse } from '../../../core/models/PageResponse';
import { ProjectService } from '../../../core/services/project.service';
import { RoleInProject } from '../../../core/enums/RoleInProject';

@Component({
  selector: 'app-project-members',
  imports: [],
  templateUrl: './project-members.html',
  styleUrl: './project-members.css',
})
export class ProjectMembers {
  members = signal<UserModel[]>([]);

  constructor(private userService : UserService, private projectService : ProjectService){
    this.userService.getAllUsers(0,20).subscribe(
      (d)=>{
        console.log(d)
        this.members.set(d.content) 
        window.alert("Fetched All Users")
      }
    )

    this.projectService.addProjectPerMember(4,RoleInProject.OWNER).subscribe(
      (data)=>{
        console.log("Member Added", data)
      }
    );
  }
}
