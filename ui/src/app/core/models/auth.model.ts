export interface AuthModel{
    username : string;
    token : string;
    role :  'ADMIN'|'USER';
}