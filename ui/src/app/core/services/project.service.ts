import { inject, Injectable } from "@angular/core";
import { environment } from "../../environment";
import { HttpClient } from "@angular/common/http";
import { ProjectModel } from "../models/project.model";
import { RoleInProject } from "../enums/RoleInProject";
import { TaskModel } from "../models/task.model";
import { PageResponse } from "../models/PageResponse";

@Injectable({
    providedIn:'root'
})

export class ProjectService{
    url : string = environment.apiUrl+"/projects";
    httpClient = inject(HttpClient);

    public createProject(project : ProjectModel){
        return this.httpClient.post<ProjectModel>(`${this.url}`, project);
    }

    public postAllProjects(project : ProjectModel[]){
        return this.httpClient.post<string>(`${this.url}/all`, project);
    }

    public getOneProject(projectId : number){
        return this.httpClient.get<ProjectModel>(`${this.url}/${projectId}`);
    }

    public updateProject(projectId : number, updateProjectRequest : ProjectModel){
        return this.httpClient.put<String>(`${this.url}/${projectId}`, updateProjectRequest);
    }

    public deleteProject(projectId : number){
        return this.httpClient.delete<String>(`${this.url}/${projectId}`);
    }

    public addProjectPerMember(projectId : number, role : RoleInProject){
        return this.httpClient.post<string>(`${this.url}/${projectId}/members`,role);
    }

    public deleteProjectForMember(projectId : number, memberId : number){
        return this.httpClient.delete<string>(`${this.url}/${projectId}/members/${memberId}`);
    }

    //TODO: Whenever we are fetching Project List we should @EntityGraph fetch Tasks too to calculate how many tasks are completed or not
    public getAllProject(){
        return this.httpClient.get<ProjectModel[]>(`${this.url}/all`);
    }
    public getAllTaskOfProject(projectId : number){
        return this.httpClient.get<PageResponse<ProjectModel>>(`${this.url}/${projectId}/tasks`);
    }
    
    public postAllTaskOfProject(projectId : number, tasks : TaskModel[]){
        return this.httpClient.post(`${this.url}/${projectId}/tasks`, tasks);
    }

    public getProjectsForUser(userId : number){
        return this.httpClient.get<ProjectModel[]>(`${this.url}/user/${userId}`);
    }

}