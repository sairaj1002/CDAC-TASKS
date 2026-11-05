// The answer of the backend to an action (save, update, approve ...) and the body of every error.
// Same fields as the original QMS Message: msg is the text of the pop-up, className its style
// (text-success or text-danger). The text is written in the backend (QmsConstants); the screens only show it.
export interface Message {
  className: string;
  msg: string;
  id?: number;
}
