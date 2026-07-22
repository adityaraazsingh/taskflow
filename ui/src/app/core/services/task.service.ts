import { inject, Injectable } from "@angular/core";
import { environment } from "../../environment";
import { HttpClient, HttpParams } from "@angular/common/http";
import { TaskModel } from "../models/task.model";
import { UserModel } from "../models/user.model";
import { CommentModel } from "../models/comment.model";
import { TagModel } from "../models/tag.model";

@Injectable({
    providedIn: 'root'
})

export class TaskService{

    url : string = environment.apiUrl+"/tasks";
    httpClient = inject(HttpClient);

    public getTaskById(id : number){
        return this.httpClient.get<TaskModel>(`${this.url}/${id}`);
    }

    // @PostMapping("project/{projectId}")
    // public ResponseEntity<TaskResponseDto> createTask(@RequestBody TaskRequestDTO dto, @PathVariable Long projectId){
    //     return ResponseEntity.status(HttpStatus.CREATED).body(taskMapper.toDto(taskService.createTask(projectId,dto)));
    // }
    public createTask(projectId:number ,task : TaskModel){
        return this.httpClient.post<TaskModel>(`${this.url}/project/${projectId}`, task);
    }

    public updateTaskById(id : number, updatedTask : TaskModel){
        return this.httpClient.put<TaskModel>(`${this.url}/${id}`, updatedTask);
    }
    
    public changeStatusOfTask(id : number, newStatus : 'TODO' | 'IN_PROGRESS' | 'DONE'){
        return this.httpClient.put<TaskModel>(`${this.url}/${id}`, newStatus);
    }

    public changeAssignee(taskId : number,user : UserModel){
        return this.httpClient.patch(`${this.url}/${taskId}/assignee`, user);
    }

    public deleteTask(taskId : number){
        return this.httpClient.delete(`${this.url}/${taskId}`);
    }

    public getComments(page : number, size : number){
        let params = new HttpParams()
            .set('page', page.toString())
            .set('size', size.toString());        
        return this.httpClient.get<CommentModel[]>(`${this.url}/comments`, {params});
    }

    public postCommentsForTask(taskId :  number, comments : CommentModel){
        return this.httpClient.post(`${this.url}/${taskId}/comments`, comments);
    }

    public addTasksPerTags(taskId : number, tagId : number, tags : TagModel[]){
        return this.httpClient.post<string>(`${this.url}/${taskId}/tags/${tagId}`, tags)
    }

    public deleteTagForTask(taskId : number){
        return this.httpClient.delete(`${this.url}/${taskId}`);
    }

}
