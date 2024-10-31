export enum Role {
  GUEST = "GUEST",
  UNVERIFIED_USER = "UNVERIFIED_USER",
  USER = "USER",
  ADMIN = "ADMIN",
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: Role;
}
