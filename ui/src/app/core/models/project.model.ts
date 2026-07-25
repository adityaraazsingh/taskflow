import { Status } from "../enums/Status";

export interface ProjectModel{
    id?: number;
    name: string;
    description: string;
    status: Status;
    // userId: number;
    taskIds?: number[];
    projectMemberIds?: number[];
    createdAt?: Date;
    updatedAt?: Date;
}