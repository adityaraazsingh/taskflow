export interface ProjectModel{
    id?: number;
    name: string;
    description: string;
    status: 'TODO' | 'IN_PROGRESS' | 'DONE';
    userId: number;
    taskIds: number[];
    projectMemberIds: number[];
}