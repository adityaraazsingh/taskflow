import { Component, input, signal } from '@angular/core';
import { CommentModel } from '../../../core/models/comment.model';
import { CommentService } from '../../../core/services/comment.service';
import { TaskService } from '../../../core/services/task.service';
import { FormsModule } from '@angular/forms';
import { AsyncPipe, DatePipe, NgClass } from '@angular/common';
import { MomentModule } from 'ngx-moment';
import { map, Observable } from 'rxjs';
import { ProfileService } from '../../../core/services/profileService';

@Component({
  selector: 'app-comment-list',
  imports: [FormsModule, MomentModule, NgClass, AsyncPipe],
  templateUrl: './comment-list.html',
  styleUrl: './comment-list.css',
})
export class CommentList {
  // allComments = signal<CommentModel[]>([]);
  allComments$ = new Observable<CommentModel[]>();
  commentText : string = '';
  taskId = input.required<number>();
  isReplying = signal<number>(-1);

  constructor(private taskService : TaskService , private commentService : CommentService, private profileService : ProfileService){}

  ngOnInit(): void {
    this.commentService.connect();
    this.commentService.loadCommentsForAllTasks([this.taskId()]);
    this.commentService.connectToTask(this.taskId());
    this.allComments$ = this.commentService.comments$.pipe(
      map(map => map.get(this.taskId()) || [])
    );
  }

  ngOnDestroy() {
    this.commentService.unsubscribeTask(this.taskId());
  }

  onClickingComment(){
    const comments : CommentModel[] = [];
    const payload : CommentModel = {
      name : this.profileService.profileSignal()?.firstName || 'Unknown',
      content : this.commentText
    }
    comments.push(payload);
    
    this.taskService.postCommentsForTask(this.taskId(),comments).subscribe(
      (next)=>{
        this.commentText='',
        this.commentService.loadCommentsForAllTasks([this.taskId()]);
      }
    );
  }

  replyingToAComment(commentId : number){
    this.isReplying.set(commentId);
  }

  // get replyingComment() {
    // return this.allComments().find(c => c.id === this.isReplying()) || null;
  // }

  onCanclingReply(){
    this.isReplying.set(-1)
  }

}
