// @ts-ignore
import React, {useEffect, useState} from "react";
import {
    Button,
    Paper,
    Alert,
    Dialog,
    DialogTitle,
    IconButton,
    DialogContent
} from "@mui/material";
import Layout from "../components/Layout";
import Toolkit from "../components/Toolkit";
import UploadButton from "../components/UploadButton";
import OntologyTreeView from "../components/OntologyTreeView";
import ClearIcon from '@mui/icons-material/Clear';
import CloseIcon from '@mui/icons-material/Close';
import {ComparisonDto, ComparisonSettings, uploadOntologies} from '../api/ontologyApi';


const IndexPage = () => {
  const [originalOntology, setOriginalOntology] = useState<File>(null);
  const [updateOntology, setUpdateOntology] = useState<File>(null);
  const [resultComparison, setResultComparison] = useState<ComparisonDto>(null);

  const [error, setError] = useState<string>(null);

  const [expanded, setExpanded] = useState<string[]>([]);
  const [toAddFromOriginal, setToAddFromOriginal] = useState<string[]>([]);
  const [toDeleteFromUpdate, setToDeleteFromUpdate] = useState<string[]>([]);
  const [openMergeModal, setOpenMergeModal] = useState<boolean>(false);
  const [comparisonSettings, setComparisonSettings] = useState<ComparisonSettings>(
      {diffType: "SYNTACTIC",
        diffView: "CLASSIFIED_FRAME_VIEW",
        syntax:"MANCHESTER",
        generateExplanation: false,
        colors: {common: 'black', cex: 'orange', inferred: 'red'}}
  );

  useEffect(()=>{
    if(originalOntology!=null && updateOntology!=null){
      uploadOntologies(originalOntology,updateOntology,comparisonSettings).then(res =>{
        setResultComparison(res);
        setComparisonSettings({...comparisonSettings, sid: res.sessionId})
        setError(null)
      }).catch(err =>{
        setError(err.response.data.message);
      })
    }
  },[originalOntology, updateOntology, comparisonSettings.syntax, comparisonSettings.diffView, comparisonSettings.diffType, comparisonSettings.generateExplanation])

  const clearOntologies = () => {
    setOriginalOntology(null);
    setUpdateOntology(null);
    setResultComparison(null);
    setComparisonSettings({...comparisonSettings, sid: null, generateExplanation: false, diffType: "SYNTACTIC"})
  }

  const onMerge = () => {
    setOpenMergeModal(true);
  }

  return (
    <Layout pageTitle="OWLDiff">
      <Toolkit comparisonSettings={comparisonSettings} setComparisonSettings={setComparisonSettings} onMerge={onMerge}/>
      {error && <Alert sx={{margin: 4, width: 500, marginLeft: '30%'}} severity="error">{error}</Alert>}
      <div style={{display: "flex", flexFlow: "row", justifyContent: "space-between", marginLeft: '10%'}}>
        <UploadButton setParentUploadedFile={setOriginalOntology} parentUploadedFile={originalOntology} text="Upload original ontology"/>
        <UploadButton setParentUploadedFile={setUpdateOntology} parentUploadedFile={updateOntology} text="Upload updated ontology"/>
        <Button variant="contained" component="span" startIcon={<ClearIcon/>} onClick={clearOntologies}>
          Clear
        </Button>
      </div>
      <div style={{display: "flex", flexFlow: "row", justifyContent: "flex-start", marginTop: 20}}>
        {resultComparison!=null &&
          <>
            <Paper sx={{width:'45%', wordWrap:"break-word"}} elevation={24}>
                {resultComparison.original.name}
                <OntologyTreeView treeItems={resultComparison.original.data}
                                  expanded={expanded}
                                  setExpanded={setExpanded}
                                  colorSettings={comparisonSettings.colors}
                                  setSelected={setToAddFromOriginal}/>
            </Paper>
            <Paper sx={{width:'45%', wordWrap:"break-word", marginLeft: 2}} elevation={24}>
                {resultComparison.update.name}
                <OntologyTreeView treeItems={resultComparison.update.data}
                                  expanded={expanded}
                                  setExpanded={setExpanded}
                                  colorSettings={comparisonSettings.colors}
                                  setSelected={setToDeleteFromUpdate}/>
            </Paper>
          </>
        }
      </div>

        {/*TODO: CREATE MERGE MODAL */}
        <Dialog open={openMergeModal} onClose={()=>{setOpenMergeModal(false)}}>
            <DialogTitle sx={{width: 500, display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                <h2>Merge ontologies</h2>
                <IconButton onClick={()=>{setOpenMergeModal(false)}}>
                    <CloseIcon />
                </IconButton>
            </DialogTitle>
            <DialogContent>
                <span>Dialog Content</span>
            </DialogContent>
        </Dialog>
    </Layout>
  )
}

export default IndexPage
