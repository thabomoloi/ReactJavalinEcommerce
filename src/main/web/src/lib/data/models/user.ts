import { Role, User } from "./types";

export class UserImpl implements User {
  _id: number;
  _name: string;
  _email: string;
  _role: Role;

  constructor(id: number, name: string, email: string, role: Role) {
    this._id = id;
    this._name = name;
    this._email = email;
    this._role = role;
  }

  public get id(): number {
    return this._id;
  }

  public get name(): string {
    return this._name;
  }

  public get email(): string {
    return this._email;
  }

  public get role(): Role {
    return this._role;
  }
}

export function stringToRole(value: string, defaultValue: Role): Role {
  if (Object.values(Role).includes(value as Role)) {
    return Role[value as Role];
  }
  return defaultValue;
}
