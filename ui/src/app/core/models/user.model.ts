export interface UserModel{
    id? : number,
    username : string ,
    password? : string, //TODO: we have to set the password but afterwards its optional
    role : string,
    email: string
    taskIds? : number[],
    commentIds? : number[],
    projectMemberIds? : number[],
    projectIds? : number[],
    createdAt? : Date
}