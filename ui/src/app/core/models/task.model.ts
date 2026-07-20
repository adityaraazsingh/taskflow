import { UserModel } from "./user.model";

export interface TaskModel {
    id?: number;
    title: string;
    description: string;
    assignee: UserModel;
    dueDate: Date;
    tagsIds: number[];
    projectIds? : number[]; // projectId is being send in Url so it gets added in the backend
    comments? : number[];
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';   
    updatedAt?: Date;
    createdAt?: Date;
}