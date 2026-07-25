import { RoleInProject } from "../enums/RoleInProject";

export interface assigningUserRequestDto {
    roleInProject : RoleInProject ;
    userId : number ;
}