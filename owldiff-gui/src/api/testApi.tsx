import APIKit from "./Base";

export const ping = () => {
    return APIKit.get("/ping")
    .then(response => {
        return response.data;
    }).catch(error => {
        throw(error);
    })
}