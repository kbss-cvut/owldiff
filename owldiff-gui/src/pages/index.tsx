// @ts-ignore
import React, {useEffect, useState} from "react";
import {
    Button,
    Paper,
    Alert,
    CircularProgress
} from "@mui/material";
import Layout from "../components/Layout";
import Toolkit from "../components/Toolkit";
import UploadButton from "../components/UploadButton";
import OntologyTreeView from "../components/OntologyTreeView";
import ClearIcon from '@mui/icons-material/Clear';
import { ComparisonDto, ComparisonSettings, getComparisonResult, uploadOntologies } from '../api/ontologyApi';
import { MergeDialog } from "../components/MergeDialog";
import useCountDown from 'react-countdown-hook';
// @ts-ignore
import * as stylesComponents from "../components/Components.module.css";
// @ts-ignore
import * as styles from "./Pages.module.css";


const IndexPage = () => {
  const [timeLeft, { start }] = useCountDown(60000, 1000);
  const [originalOntology, setOriginalOntology] = useState<File>(null);
  const [updateOntology, setUpdateOntology] = useState<File>(null);
  const [resultComparison, setResultComparison] = useState<ComparisonDto>(null);

  const [error, setError] = useState<string>(null);
  const [loading, setLoading] = useState<boolean>(false);

  const [expanded, setExpanded] = useState<string[]>([]);
  const [openMergeModal, setOpenMergeModal] = useState<boolean>(false);
  const [comparisonSettings, setComparisonSettings] = useState<ComparisonSettings>(
      {diffType: "SYNTACTIC",
        diffView: "LIST_VIEW",
        syntax:"MANCHESTER",
        generateExplanation: false,
        showCommon: false,
        colors: {common: 'black', cex: 'orange', inferred: 'red'}}
  );

  useEffect(() => {
      let curSid = localStorage.getItem('sid');
      if(typeof window !==`undefined`){
          const queryString = window.location.search;
          const urlParams = new URLSearchParams(queryString);
          if(urlParams.get("sid")!=null){
              curSid = urlParams.get("sid");
              setLoading(true);
              getComparisonResult(curSid).then(res => {
                  setResultComparison(res);
                  start(res.sessionTimer * 1000)
                  setLoading(false);
              }).catch(err =>{
                  setError(err.response.data.message);
                  setLoading(false);
              })
          }
      }
      if(curSid!=undefined && curSid!=""){
        setComparisonSettings({...comparisonSettings, sid: curSid})
      }
  },[])

  useEffect(()=>{
    if(originalOntology!=null && updateOntology!=null){
      setLoading(true);
      uploadOntologies(originalOntology,updateOntology,comparisonSettings).then(res =>{
        setResultComparison(res);
        start(res.sessionTimer * 1000)
        setLoading(false);
        localStorage.setItem('sid',res.sessionId);
        setComparisonSettings({...comparisonSettings, sid: res.sessionId})
        setError(null)
      }).catch(err =>{
        setError(err.response.data.message);
        setLoading(false);
      })
    }
  },[originalOntology, updateOntology, comparisonSettings.syntax, comparisonSettings.diffView, comparisonSettings.diffType, comparisonSettings.generateExplanation, comparisonSettings.showCommon])

  const clearOntologies = () => {
    setOriginalOntology(null);
    setUpdateOntology(null);
    setResultComparison(null);
    setLoading(false);
    localStorage.setItem('sid',"");
    setError(null);
    setComparisonSettings({...comparisonSettings, sid: undefined, generateExplanation: false, diffType: "SYNTACTIC"})
  }

  const onMerge = () => {
    setOpenMergeModal(true);
  }

  return (
    <Layout pageTitle="OWLDiff">
      <Toolkit comparisonSettings={comparisonSettings} setComparisonSettings={setComparisonSettings} onMerge={onMerge} disabled={loading}/>
      {error && <Alert sx={{margin: 4, width: 500, marginLeft: '30%'}} severity="error">{error}</Alert>}
      <div className={styles.buttons_div}>
        <UploadButton setParentUploadedFile={setOriginalOntology} parentUploadedFile={originalOntology} text="Upload original ontology"/>
        <UploadButton setParentUploadedFile={setUpdateOntology} parentUploadedFile={updateOntology} text="Upload updated ontology"/>
        <Button sx={{minWidth: 'inherit'}} variant="contained" component="span" startIcon={<ClearIcon/>} onClick={clearOntologies}>
          Clear
        </Button>
      </div>
        {loading == true ? <CircularProgress sx={{marginLeft: 'calc(50% + 40px)', marginTop: 20}} /> :
          <>
          <div className={stylesComponents.paper_flex}>
            {resultComparison!=null &&
              <>
                <Paper sx={{width:'45%', wordWrap:"break-word", minWidth: '300px'}} elevation={24}>
                    <OntologyTreeView treeItems={resultComparison.original.data}
                                      expanded={expanded}
                                      setExpanded={setExpanded}
                                      colorSettings={comparisonSettings.colors}/>
                </Paper>
                <Paper sx={{width:'45%', wordWrap:"break-word", marginLeft: 2, minWidth: '300px'}} elevation={24}>
                    <OntologyTreeView treeItems={resultComparison.update.data}
                                      expanded={expanded}
                                      setExpanded={setExpanded}
                                      colorSettings={comparisonSettings.colors}/>
                </Paper>
              </>
            }
          </div>
            {resultComparison &&
                <MergeDialog openMergeModal={openMergeModal} setOpenMergeModal={setOpenMergeModal}
                             resultComparison={resultComparison} expanded={expanded} sessionTimer={timeLeft}/>
            }
        </>
        }
    </Layout>
  )
}

export default IndexPage
