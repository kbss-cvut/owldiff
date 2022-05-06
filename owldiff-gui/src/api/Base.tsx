import axios from 'axios';

const APIKit = axios.create({
    baseURL: process.env.GATSBY_API_URL,
    headers: {
        'Access-Control-Allow-Origin':process.env.GATSBY_CLIENT_URL,
        post:{
            'Content-Type':  'application/json'
        },
    }
});

export default APIKit;