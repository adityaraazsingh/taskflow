import { Component, input, signal } from '@angular/core';
import { CommentModel } from '../../../core/models/comment.model';
import { CommentService } from '../../../core/services/comment.service';
import { TaskService } from '../../../core/services/task.service';
import { FormsModule } from '@angular/forms';
import { DatePipe, NgClass } from '@angular/common';
import { MomentModule } from 'ngx-moment';

@Component({
  selector: 'app-comment-list',
  imports: [FormsModule, MomentModule, NgClass],
  templateUrl: './comment-list.html',
  styleUrl: './comment-list.css',
})
export class CommentList {
  // comments = input<CommentModel[]>([]);
  allComments= signal<CommentModel[]>([]);
  commentText : string = '';
  taskId = input.required<number>();
  // isReplying = signal<boolean>(false);
  isReplying = signal<number>(-1);

  constructor(private taskService : TaskService , private commentService : CommentService){}

  ngOnInit(): void {
    this.taskService.getCommentsForTask(this.taskId()! , 0 , 10).subscribe(
      next =>{
        this.allComments.set(next.content)
      }
    );

    this.commentService.connect((comment) => {
      this.allComments.update(current => [
        ...current,comment
      ]);
    });
  }

  onClickingComment(){
    const comments : CommentModel[] = [];
    const payload : CommentModel = {
      content : this.commentText
    }
    comments.push(payload);
    
    this.taskService.postCommentsForTask(this.taskId(),comments).subscribe(
      (next)=>{
        console.log(next)
        this.commentText=''
      }
    );
  }

  replyingToAComment(commentId : number){
    this.isReplying.set(commentId);
  }

  get replyingComment() {
    return this.allComments().find(c => c.id === this.isReplying()) || null;
  }

  onCanclingReply(){
    this.isReplying.set(-1)
  }

}
