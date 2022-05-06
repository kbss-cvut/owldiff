import { Button } from '@mui/material';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import * as React from 'react';

interface UploadButtonProps{
    text: string,
    parentUploadedFile: File,
    setParentUploadedFile: (File) => void,
}

const UploadButton = (props: UploadButtonProps) => {
    const [isFileUploaded, setIsFileUploaded] = React.useState(props.parentUploadedFile!=null);

	const changeHandler = (event : React.ChangeEvent<HTMLInputElement>) => {
        props.setParentUploadedFile(event.target.files[0]);
        event.target.value = null;
		setIsFileUploaded(true);
	};

    React.useEffect(()=>{
        if(props.parentUploadedFile==null) setIsFileUploaded(false);
    },[props.parentUploadedFile])

    return (
        <div>
        <input
            accept=".owl, .obo, .ttl, .owx, .omn, .ofn"
            style={{display: 'none'}}
            id={props.text}
            type="file"
            onChange={changeHandler}
        />
        <label htmlFor={props.text}>
            {props.parentUploadedFile!=null ? props.parentUploadedFile.name :
            <Button variant="contained" component="span" startIcon={<UploadFileIcon/>}>
                {props.text}
            </Button>}
        </label>
        </div>
    )
}

export default UploadButton