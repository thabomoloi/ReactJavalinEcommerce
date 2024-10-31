import axios from "axios";
import { Role, User } from "../models/types";
import { CURRENT_USER_URL, REFRESH_TOKEN_URL } from "@/lib/urls";
import { stringToRole, UserImpl } from "../models/user";

async function getCurrentUser(): Promise<User | null> {
  const response = await axios.get(CURRENT_USER_URL);
  if (response.status == 200) {
    const data = response.data;
    return new UserImpl(
      data.id,
      data.name,
      data.email,
      stringToRole(data.role, Role.GUEST)
    );
  }
  return null;
}

async function refreshJWT(): Promise<void> {
  await axios.post(REFRESH_TOKEN_URL);
}

export { getCurrentUser, refreshJWT };