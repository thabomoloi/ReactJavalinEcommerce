import {
  deleteAccount,
  signIn,
  signUp,
  updateProfile,
} from "@/lib/data/api/user";
import {
  SignInSchemaType,
  SignUpSchemaType,
  UserUpdateSchemaType,
} from "@/lib/data/schemas/user";
import { AxiosError } from "axios";
import { ActionFunctionArgs, json, redirect } from "react-router-dom";

// interface Updates {
//   [key: string]: FormDataEntryValue; // This covers form data entries which could be strings or File objects
// }

export async function signUpAction({ request }: ActionFunctionArgs) {
  try {
    const data = (await request.json()) as SignUpSchemaType;
    const message = await signUp(data);
    return json({ error: false, message });
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error.response?.status;
      const data = error.response?.data;
      if (data?.title && status && status >= 400 && status < 500) {
        return json({ error: true, message: data.title });
      }
    }
    throw error;
  }
}

export async function signInAction({ request }: ActionFunctionArgs) {
  try {
    const data = (await request.json()) as SignInSchemaType;
    const message = await signIn(data);
    return json({ error: false, message });
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error.response?.status;
      const data = error.response?.data;
      if (data?.title && status && status >= 400 && status < 500) {
        return json({ error: true, message: data.title });
      }
    }
    throw error;
  }
}

export async function profileAction({ request }: ActionFunctionArgs) {
  try {
    const data = (await request.json()) as UserUpdateSchemaType;
    const message = await updateProfile(data);
    return json({ error: false, message });
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error.response?.status;
      const data = error.response?.data;
      if (data?.title && status && status >= 400 && status < 500) {
        return json({ error: true, message: data.title });
      }
    }
    throw error;
  }
}

export async function deleteAccountAction({ params }: ActionFunctionArgs) {
  try {
    if (params.userId) {
      await deleteAccount(parseInt(params.userId));
      return redirect("/");
    }
    return redirect("/account/profile");
  } catch (error) {
    if (error instanceof AxiosError) {
      const status = error.response?.status;
      const data = error.response?.data;
      if (data?.title && status && status >= 400 && status < 500) {
        return json({ error: true, message: data.title });
      }
    }
    throw error;
  }
}
