import axios from "axios";
import { Role, User } from "../models/types";
import {
  CONFIRM_URL,
  CURRENT_USER_URL,
  REFRESH_TOKEN_URL,
  RESET_PASSWORD_URL,
  SIGN_IN_URL,
  SIGN_OUT_URL,
  SIGN_UP_URL,
  USERS_URL,
} from "@/lib/urls";
import { stringToRole, UserImpl } from "../models/user";
import {
  ForgotPasswordSchemaType,
  ResetPasswordSchemaType,
  SignInSchemaType,
  SignUpSchemaType,
  UserUpdateSchemaType,
} from "../schemas/user";

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

async function signUp(data: SignUpSchemaType): Promise<string> {
  const response = await axios.post(SIGN_UP_URL, data);
  return response.data as string;
}

async function signIn(data: SignInSchemaType): Promise<string> {
  const response = await axios.post(SIGN_IN_URL, data);
  return response.data as string;
}

async function signOut(): Promise<string> {
  const response = await axios.delete(SIGN_OUT_URL);
  return response.data as string;
}

async function updateProfile(data: UserUpdateSchemaType): Promise<string> {
  const response = await axios.patch(USERS_URL + "/" + data.id, data);
  return response.data as string;
}

async function deleteAccount(userId: number) {
  const response = await axios.delete(USERS_URL + "/" + userId);
  return response.data as string;
}

async function sendConfirmationLink(userId: number) {
  const response = await axios.post(CONFIRM_URL + "/" + userId);
  return response.data as string;
}

async function confirmAccount(token: string) {
  const response = await axios.patch(CONFIRM_URL + "/" + token);
  return response.data as string;
}

async function sendResetPasswordLink(data: ForgotPasswordSchemaType) {
  const response = await axios.post(RESET_PASSWORD_URL, data);
  return response.data as string;
}

async function resetPassword({
  token,
  data,
}: {
  token: string;
  data: ResetPasswordSchemaType;
}) {
  const response = await axios.patch(RESET_PASSWORD_URL + "/" + token, data);
  return response.data as string;
}

export {
  getCurrentUser,
  refreshJWT,
  signUp,
  signIn,
  signOut,
  updateProfile,
  deleteAccount,
  sendConfirmationLink,
  confirmAccount,
  sendResetPasswordLink,
  resetPassword,
};
