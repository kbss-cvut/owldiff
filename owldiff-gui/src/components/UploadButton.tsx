import { Button, IconButton } from '@mui/material';
import { makeStyles } from '@mui/styles';
import UploadFileIcon from '@mui/icons-material/UploadFile';
import * as React from 'react';
import { uploadedOntology } from '../pages';

interface UploadButtonProps{
    text: string,
    type?: "original" | "updated",
    parentUploadedFiles?: uploadedOntology[],
    setParentUploadedFiles?: any,
}

const useStyles = makeStyles({
    input: {
      display: 'none',
    },
});

const UploadButton = (props: UploadButtonProps) => {
    const [uploadedFile, setUploadedFile] = React.useState<File>(null);
    const [isFileUploaded, setIsFileUploaded] = React.useState(false);
    const classes = useStyles();

	const changeHandler = (event : React.ChangeEvent<HTMLInputElement>) => {
		setUploadedFile(event.target.files[0]);
        props.setParentUploadedFiles(parentUploadedFiles => [...parentUploadedFiles, {type:props.type, file:event.target.files[0]}]);
		setIsFileUploaded(true);
	};

    React.useEffect(()=>{
        if(props.parentUploadedFiles.length==0) setIsFileUploaded(false);
    },[props.parentUploadedFiles])

    return (
        <div>
        <input
            accept=".owl"
            className={classes.input}
            id={props.text}
            type="file"
            onChange={changeHandler}
        />
        <label htmlFor={props.text}>
            {isFileUploaded? uploadedFile.name : 
            <Button variant="contained" component="span" startIcon={<UploadFileIcon/>}>
                {props.text}
            </Button>}
        </label>
        </div>
    )
}

export default UploadButton