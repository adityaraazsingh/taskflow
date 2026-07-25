import { Component, signal } from '@angular/core';
import { NgClass } from '@angular/common';
import { UserService } from '../../../core/services/user.service';
import { UserModel } from '../../../core/models/user.model';
import { PageResponse } from '../../../core/models/PageResponse';
import { ProjectService } from '../../../core/services/project.service';
import { RoleInProject } from '../../../core/enums/RoleInProject';
import { ActivatedRoute } from '@angular/router';
import { assigningUserRequestDto } from '../../../core/models/assigningUserRequestDto';
import { projectMemberResponseDto } from '../../../core/models/projectMemberResponseDto';
import { FormsModule } from '@angular/forms';
import { Status } from '../../../core/enums/Status';

@Component({
  selector: 'app-project-members',
  imports: [NgClass, FormsModule],
  templateUrl: './project-members.html',
  styleUrl: './project-members.css',
})
export class ProjectMembers {
  role! : RoleInProject;
  projectId =  signal<number | null>(null);
  members = signal<projectMemberResponseDto[]>([]);
  allUsers = signal<UserModel[] | null>(null)

  constructor(private userService : UserService, private projectService : ProjectService, private route : ActivatedRoute){
    this.projectId.set(+this.route.snapshot.paramMap.get('id')!);
    this.getMembersForCurrProject()
    this.userService.getAllUsers(0,20).subscribe(
      data =>{
        this.allUsers.set(data.content)
      }
    )
  }

  getMembersForCurrProject(){
    this.projectService.getAllUsersForAProject(this.projectId()!).subscribe(
      (d)=>{
        this.members.set(d) 
      }
    )
  }

  deleteMemberFromProject(memberId : number){
    this.projectService.deleteProjectForMember(this.projectId()! , memberId).subscribe(
      (next)=>{
        console.log(next),
        this.getMembersForCurrProject()
      }
    )
  }

  addMemberOnAProject(userId : number ){
    const payload : assigningUserRequestDto = {
      roleInProject : this.role,
      userId : userId
    }

    this.projectService.addProjectPerMember(this.projectId()! ,payload).subscribe(
      (next)=>{
        this.getMembersForCurrProject()
      }
    )
  }
}
