export class UserModel {

    public id: number;
    public accountId: number;
    public roleId: number;

    public token: string;
    public key: string;
    public role: string;
    public email: string        = '';
    public firstName: string    = '';
    public lastName: string     = '';
    public advertiserIds: Array<number>  = [];
}