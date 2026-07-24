import { Priority } from "../enums/Priority";
import { Status } from "../enums/Status";
import { UserModel } from "./user.model";

export interface TaskModel {
    id?: number;
    title: string;
    description: string;
    assigneeId?: number;
    dueDate: Date;
    tagsIds?: number[];
    projectId? : number; // projectId is being send in Url so it gets added in the backend
    comments? : number[];
    priority: Priority;
    status: Status;   
    updatedAt?: Date;
    createdAt?: Date;
}