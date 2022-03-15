import * as React from 'react';
import {ColorsSettings, NodeModelDto} from "../api/ontologyApi";
import TreeItem from "@mui/lab/TreeItem";
import TreeView from "@mui/lab/TreeView";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// @ts-ignore
import * as styles from './Components.module.css';
import {Tooltip} from "@mui/material";

interface OntologyTreeViewProps{
    treeItems: NodeModelDto,
    colorSettings?: ColorsSettings,
    expanded?: string[],
    setExpanded?: (value: string[]) => void;
    selected?: string[],
    setSelected?: (value: string[]) => void;
}

const OntologyTreeView = (props: OntologyTreeViewProps) => {

    let usedIds: number[] = [];

    const handleExpanded = (value: string) =>{
       if(!props.expanded.includes(value)){
            props.setExpanded([...props.expanded, value]);
       }else{
            props.setExpanded(props.expanded.filter(val => val !== value));
       }
    }

    const getTreeItemsFromData = (treeItems: NodeModelDto[]) => {
        return treeItems.map(treeItemData => {
            if(usedIds.includes(treeItemData.id)){
                return;
            }
            let children = undefined;
            if (treeItemData.children && treeItemData.children.length > 0) {
                usedIds.push(treeItemData.id);
                children = getTreeItemsFromData(treeItemData.children);
            }
            return (
                <TreeItem
                    key={treeItemData.id}
                    sx={props.colorSettings ?
                        treeItemData.isCommon ? {color: props.colorSettings.common} :
                        treeItemData.isInferred ? {color: props.colorSettings.inferred} :
                        treeItemData.useCex ? {color: props.colorSettings.cex} :
                        {color: 'black'}
                        : {color: 'black'}}
                    nodeId={treeItemData.id.toString()}
                    classes={{content: styles.ontology_tree_view_item,
                              selected: styles.ontology_tree_view_item_selected}}
                    label={treeItemData.explanations ?
                    <Tooltip followCursor title={<h2>{treeItemData.explanations}</h2>}><div dangerouslySetInnerHTML={{__html: treeItemData.data}}/></Tooltip>
                    : <div dangerouslySetInnerHTML={{__html: treeItemData.data}}/>
                    }
                    children={children}
                    onClick={(props.expanded && props.setExpanded) ? () => handleExpanded(treeItemData.id.toString()) : undefined}
                />
            );
        });
    };

    return (
        <>
            <div>{props.treeItems.data}</div>
            <TreeView
                defaultCollapseIcon={<ExpandLessIcon />}
                defaultExpandIcon={<ExpandMoreIcon />}
                expanded={props.expanded}
                multiSelect
                onNodeSelect={(node, ids)=>{props?.setSelected(ids)}}
            >
                {getTreeItemsFromData(props.treeItems.children)}
            </TreeView>
        </>
    )
}

export default OntologyTreeView;