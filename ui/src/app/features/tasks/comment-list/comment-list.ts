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
  imports: [FormsModule, MomentModule, AsyncPipe],
  templateUrl: './comment-list.html',
  styleUrl: './comment-list.css',
})
export class CommentList {
  allComments$ = new Observable<CommentModel[]>();
  commentText : string = '';
  taskId = input.required<number>();
  replyComment = signal<CommentModel | null>(null);

  get currentUserInitial(): string {
    const name = this.profileService.profileSignal()?.firstName || 'U';
    return name.substring(0, 2).toUpperCase();
  }

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
      content : this.commentText,
      replyCommentContent : this.replyComment()?.content,
      replyCommentId : this.replyComment()?.id
    }
    comments.push(payload);
    
    this.taskService.postCommentsForTask(this.taskId(),comments).subscribe(
      (next)=>{
        this.commentText=''
        ,this.commentService.loadCommentsForAllTasks([this.taskId()]);
      }
    );
  }
  
  replying_action(comment : CommentModel){
    this.replyComment.set(comment);
  }
  
  deleteComment(comment : CommentModel){
    this.commentService.deleteComments(comment.id!).subscribe(
      (data) =>{
        this.commentService.loadCommentsForAllTasks([this.taskId()]);
      }
    );
  }

  onCanclingReply(){
    this.replyComment.set(null)
  }

}
