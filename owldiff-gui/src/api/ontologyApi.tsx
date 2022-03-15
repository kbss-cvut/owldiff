import { resultBothOntologies } from "../pages";
import APIKit from "./Base";

export const uploadOntologies = (data: File[]) => {
    let formData = new FormData();
    data.forEach(file => formData.append("files",file))
    return APIKit.post<resultBothOntologies>("/ontology/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        }}
    )
    .then(response => {
        return response.data;
    }).catch(error => {
        throw(error);
    })
}