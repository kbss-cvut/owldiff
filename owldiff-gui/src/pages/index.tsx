import { Button, Paper } from "@mui/material"
import * as React from "react"
import Layout from "../components/Layout"
import Toolkit from "../components/Toolkit"
import UploadButton from "../components/UploadButton"
import TreeView from '@mui/lab/TreeView';
import TreeItem from '@mui/lab/TreeItem';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandLessIcon from '@mui/icons-material/ExpandLess';
import ClearIcon from '@mui/icons-material/Clear'
import { uploadOntologies } from '../api/ontologyApi'
import { ping } from '../api/testApi'

export interface uploadedOntology{
  type: "original" | "updated",
  file: File
}

export interface resultBothOntologies{
  original: resultOntology,
  update: resultOntology,
}

export interface resultOntology{
  name: string,
  children: ontologyData[];
}

export interface ontologyData{
  data: string,
  id: number,
  children: ontologyData[]
}

const IndexPage = () => {
  const [uploadedOntologies, setUploadedOntologies] = React.useState<uploadedOntology[]>([]);  
  const [resultOntologies, setResultOntologies] = React.useState<resultBothOntologies>(null);  

  React.useEffect(()=>{
    if(uploadedOntologies.length==2){
      let tmp = uploadedOntologies.sort((first, second)=>first.type=="original"? -1 : 1);
      uploadOntologies(tmp.map(ontology => ontology.file)).then(res =>{
        setResultOntologies(res)
      }).catch(err =>{
        console.log(err)
      })
    }
  },[uploadedOntologies])

  //REWRITE THIS TREE VIEW TO NOT BE COPY
  const getTreeItemsFromData = (treeItems: ontologyData[]) => {
    return treeItems.map(treeItemData => {
      let children = undefined;
      if (treeItemData.children && treeItemData.children.length > 0) {
        children = getTreeItemsFromData(treeItemData.children);
      }
      return (
        <TreeItem
          key={treeItemData.id}
          nodeId={treeItemData.id.toString()}
          label={<div dangerouslySetInnerHTML={{__html: treeItemData.data}}></div>}
          children={children}
        />
      );
    });
  };
  const DataTreeView = ({ treeItems }) => {
    return (
      <TreeView
        defaultCollapseIcon={<ExpandLessIcon />}
        defaultExpandIcon={<ExpandMoreIcon />}
      >
        {getTreeItemsFromData(treeItems)}
      </TreeView>
    );
  };

  return (
    <Layout pageTitle="OWLDiff">
      <Toolkit/>
      <div style={{display: "flex", flexFlow: "row", justifyContent: "space-evenly"}}>
        <UploadButton type="original" setParentUploadedFiles={setUploadedOntologies} parentUploadedFiles={uploadedOntologies} text="Upload original ontology"/>
        <UploadButton type="updated" setParentUploadedFiles={setUploadedOntologies} parentUploadedFiles={uploadedOntologies} text="Upload updated ontology"/>
        <Button variant="contained" component="span" startIcon={<ClearIcon/>} onClick={()=>setUploadedOntologies([])}>
          Clear
        </Button>
      </div>
      <div style={{display: "flex", flexFlow: "row", justifyContent: "space-evenly"}}>
        {resultOntologies!=null && 
          <>
            <Paper sx={{width:600, wordWrap:"break-word"}} elevation={24}>
                {resultOntologies.original.name}
                <DataTreeView treeItems={resultOntologies.original.children} />
            </Paper>
            <Paper sx={{width:600, wordWrap:"break-word"}} elevation={24}>
                {resultOntologies.update.name}
                <DataTreeView treeItems={resultOntologies.update.children} />
            </Paper>
          </>
        }
      </div>
    </Layout>
  )
}

export default IndexPage
