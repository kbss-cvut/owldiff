// @ts-ignore
import React, {useEffect, useState} from "react";
import {
    Alert,
    Button,
    Dialog,
    DialogContent,
    DialogTitle, FormControl, FormHelperText,
    IconButton, InputLabel, MenuItem,
    Paper, Select,
    TextField,
    Typography
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import {ComparisonDto, mergeOntologies, NodeModelDto, OWLDocumentFormats} from "../api/ontologyApi";
import OntologyTreeView from "./OntologyTreeView";
// @ts-ignore
import * as styles from './Components.module.css';
import {saveAs} from 'file-saver';

interface MergeDialogProps{
    openMergeModal: boolean;
    sessionTimer: number;
    setOpenMergeModal: (value: boolean) => void;
    resultComparison: ComparisonDto;
    expanded: string[],
}

export const MergeDialog = (props: MergeDialogProps) => {
    const [toAddFromOriginal, setToAddFromOriginal] = useState<string[]>([]);
    const [toDeleteFromUpdate, setToDeleteFromUpdate] = useState<string[]>([]);
    const [fileName, setFileName] = useState<string>(undefined);
    const [format, setFormat] = useState<OWLDocumentFormats>(OWLDocumentFormats.OWL);

    const [error, setError] = useState<string>(null);
    const [mergeLoading, setMergeLoading] = useState<boolean>(false);
    const [expandedNew, setExpandedNew] = useState<string[]>(props.expanded);
    useEffect(()=>{
        setExpandedNew(props.expanded)
    },[props.expanded])

    const handleMerge = () => {
        setMergeLoading(true);
        mergeOntologies(props.resultComparison.sessionId, toAddFromOriginal, toDeleteFromUpdate, format, fileName).then(res => {
            let headerLine = res.headers['content-disposition'];
            let startFileNameIndex = headerLine.indexOf('=') + 1
            let endFileNameIndex = headerLine.length
            let filename = headerLine.substring(startFileNameIndex, endFileNameIndex);
            saveAs(new Blob([res.data], {type: "text/plain;charset=utf-8"}),filename);
            setMergeLoading(false);
            props.setOpenMergeModal(false);
        }).catch(err =>{
            setError(err.response.data.message);
        })
    }

    const findInComparisonById = (id: string, data: NodeModelDto) => {
        if(data.id.toString() == id){
            return data.data;
        }
        if(data.children){
            for (const child of data.children) {
                const found = findInComparisonById(id, child);
                if (found) {
                    return found;
                }
            }
        }
    }

    const [step, setStep] = useState<number>(1);

    return(
        <Dialog fullWidth maxWidth={"lg"} open={props.openMergeModal} onClose={()=>{props.setOpenMergeModal(false)}}>
            <DialogTitle sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                Merge ontologies
                <div className={styles.flex_row}>
                    <Typography>Session time left: {props.sessionTimer/1000} seconds</Typography>
                    <IconButton onClick={()=>{props.setOpenMergeModal(false)}}>
                        <CloseIcon />
                    </IconButton>
                </div>
            </DialogTitle>
            {error && <Alert sx={{margin: 4, width: 500, marginLeft: '30%'}} severity="error">{error}</Alert>}
            {step==1 &&
                <DialogContent>
                    <Typography variant={"body1"}>Select axioms to add from original and axioms to delete from update</Typography>
                    <div className={styles.paper_flex}>
                        <Paper sx={{width:'45%', wordWrap:"break-word"}} elevation={24}>
                            <OntologyTreeView treeItems={props.resultComparison.original.data}
                                              expanded={expandedNew}
                                              setExpanded={setExpandedNew}
                                              selected={toAddFromOriginal}
                                              setSelected={setToAddFromOriginal}/>
                        </Paper>
                        <Paper sx={{width:'45%', wordWrap:"break-word", marginLeft: 2}} elevation={24}>
                            <OntologyTreeView treeItems={props.resultComparison.update.data}
                                              expanded={expandedNew}
                                              setExpanded={setExpandedNew}
                                              selected={toDeleteFromUpdate}
                                              setSelected={setToDeleteFromUpdate}/>
                        </Paper>
                    </div>
                    <div className={styles.dialog_buttons}>
                        <Button variant={"contained"} onClick={()=>setStep(2)}>Continue</Button>
                    </div>
                </DialogContent>
            }
            {step == 2 &&
                <DialogContent>
                    <Typography variant={"body1"}>You selected following axioms</Typography>
                    <div className={styles.paper_flex}>
                        <Paper sx={{padding: 2}}>
                            <Typography variant={"body1"}>Axioms to add from original:</Typography>
                            <ul>
                            {toAddFromOriginal.map(axiom => {
                                return (
                                    <li key={axiom}><div dangerouslySetInnerHTML={{__html: findInComparisonById(axiom, props.resultComparison.original.data)}}/></li>
                                )
                            })}
                            </ul>
                        </Paper>
                        <Paper sx={{marginLeft: 2, padding: 2}}>
                            <Typography variant={"body1"}>Axioms to delete from update:</Typography>
                            <ul>
                                {toDeleteFromUpdate.map(axiom => {
                                    return (
                                        <li key={axiom}><div dangerouslySetInnerHTML={{__html: findInComparisonById(axiom, props.resultComparison.update.data)}}/></li>
                                    )
                                })}
                            </ul>
                        </Paper>
                    </div>
                    <TextField
                        variant={"outlined"}
                        label={"Filename"}
                        helperText={"Enter filename. Dont enter extension. Leave blank to use update ontology filename."}
                        value={fileName}
                        sx={{ marginTop: 2, marginRight: 2 }}
                        onChange={(file) => setFileName(file.target.value)}
                    />
                    <FormControl sx={{marginTop: 2}}>
                        <InputLabel>File format</InputLabel>
                        <Select variant={"outlined"}
                                label={"File format"}
                                value={OWLDocumentFormats[format]}
                                onChange={(e) => setFormat(e.target.value as OWLDocumentFormats)}>
                            {Object.values(OWLDocumentFormats).map(value => { return (
                                <MenuItem value={value} key={value}>{OWLDocumentFormats[value]}</MenuItem>
                            )})}
                        </Select>
                        <FormHelperText>Select file format. Result will have its extension. Default is OWL</FormHelperText>
                    </FormControl>
                    <div className={styles.dialog_buttons}>
                        <Button variant={"contained"} onClick={()=>setStep(1)}>Back</Button>
                        <Button variant={"contained"} onClick={handleMerge} disabled={mergeLoading}>Merge</Button>
                    </div>
                </DialogContent>
            }
        </Dialog>
    )
}