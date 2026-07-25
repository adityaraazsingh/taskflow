export interface CommentModel{
    id?: number;
    name: string;
    content: string;
    taskId?: number;
    userId?: number;
}