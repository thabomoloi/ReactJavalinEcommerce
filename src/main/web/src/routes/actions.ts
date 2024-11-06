import {
  deleteAccount,
  sendConfirmationLink,
  signIn,
  signOut,
  signUp,
  updateProfile,
  verifyAccount,
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

export async function signOutAction({ request }: ActionFunctionArgs) {
  if (request.method !== "DELETE") {
    return json({ error: "Method Not Allowed" }, { status: 405 });
  }

  try {
    await signOut();
    return redirect("/");
  } catch (error) {
    console.error("Sign-out error:", error);
    return redirect("/");
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

export async function sendConfirmationLinkAction({
  request,
}: ActionFunctionArgs) {
  try {
    const formData = await request.formData();
    const userId = formData.get("userId")?.toString();
    if (userId) {
      const message = await sendConfirmationLink(parseInt(userId));
      return json({ error: false, message });
    }
    return { error: true, message: "Missing userId." };
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

export async function verifyAccountAction({ params }: ActionFunctionArgs) {
  try {
    if (params.userId && params.token) {
      const userId = parseInt(params.userId);
      const message = await verifyAccount(userId, params.token);
      return json({ error: false, message });
    }
    return { error: true, message: "Missing userId or token." };
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
