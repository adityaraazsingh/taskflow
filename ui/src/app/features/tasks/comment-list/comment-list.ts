import { Component, input, signal } from '@angular/core';
import { CommentModel } from '../../../core/models/comment.model';
import { CommentService } from '../../../core/services/comment.service';
import { TaskService } from '../../../core/services/task.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-comment-list',
  imports: [FormsModule],
  templateUrl: './comment-list.html',
  styleUrl: './comment-list.css',
})
export class CommentList {
  // comments = input<CommentModel[]>([]);
  allComments= signal<CommentModel[]>([]);
  commentText : string = '';
  taskId = input.required<number>();

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
      name : this.commentText.substring(5),
      content : this.commentText
    }
    comments.push(payload);
    
    this.taskService.postCommentsForTask(this.taskId(),comments).subscribe(
      (next)=>{
        console.log(next)
      }
    );
  }
}
