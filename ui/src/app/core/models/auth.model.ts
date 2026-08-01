export interface AuthModel{
    username : string;
    accessToken : string;
    refreshToken : string;
    role :  'ADMIN'|'USER';
}