import { RoleInProject } from "../enums/RoleInProject";
import { UserModel } from "./user.model";

export interface projectMemberResponseDto{
    id : number,
    projectId : number,
    user : UserModel,
    roleInProject : RoleInProject
}