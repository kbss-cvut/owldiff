import * as React from 'react';
import {ColorsSettings, NodeModelDto} from "../api/ontologyApi";
import TreeItem from "@mui/lab/TreeItem";
import TreeView from "@mui/lab/TreeView";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// @ts-ignore
import * as styles from './Components.module.css';
import {Checkbox, Tooltip, FormControlLabel, Typography} from "@mui/material";

interface OntologyTreeViewProps{
    treeItems: NodeModelDto,
    colorSettings?: ColorsSettings,
    expanded: string[],
    setExpanded: (value: string[]) => void;
    selected?: string[],
    setSelected?: (value: string[]) => void;
}

const OntologyTreeView = (props: OntologyTreeViewProps) => {

    let usedIds: number[] = [];

    const handleExpanded = (event, nodes: string[]) =>{
        props.setExpanded(nodes);
    }

    const handleExpandedCheckbox = (event, nodes: string[]) => {
        if (event.target.closest(".MuiTreeItem-iconContainer")) {
            props.setExpanded(nodes);
        }
    }

    function getOnChange(checked: boolean, treeItem: NodeModelDto) {
        let array = checked
            ? [...props.selected, treeItem.id.toString()]
            : props.selected.filter((value) => value != treeItem.id.toString());

        props.setSelected(array);
    }

    const getCheckboxLabel = (treeItem: NodeModelDto) => {
        return(
            <FormControlLabel
                sx={{ alignItems: 'flex-start' }}
                control={
                    <Checkbox
                        sx={{ paddingTop: 0}}
                        checked={props.selected.some((item) => item === treeItem.id.toString())}
                        onChange={(event) =>
                            getOnChange(event.target.checked, treeItem)
                        }
                        onClick={(e) => e.stopPropagation()}
                    />
                }
                label={treeItem.explanations ?
                    getExplanationsLabel(treeItem.explanations, treeItem.data)
                    : <div dangerouslySetInnerHTML={{__html: treeItem.data}}/>}
                key={treeItem.id}
            />
        )
    }

    const getExplanationsLabel = (explanation: string, data: string) => {
        return(
            <Tooltip followCursor title={<h2>{explanation}</h2>}><div dangerouslySetInnerHTML={{__html: data}}/></Tooltip>
        )
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
                        treeItemData.useCex ? {color: props.colorSettings.cex} :
                        treeItemData.inferred ? {color: props.colorSettings.inferred} :
                        treeItemData.common ? {color: props.colorSettings.common} :
                        {color: 'green'}
                        : undefined}
                    nodeId={treeItemData.id.toString()}
                    classes={{content: styles.ontology_tree_view_item,
                              selected: styles.ontology_tree_view_item_selected}}
                    label={
                        props.setSelected != undefined ?
                            (treeItemData.isAxiom == true && treeItemData.common == false)
                                ? getCheckboxLabel(treeItemData)
                                : <div dangerouslySetInnerHTML={{__html: treeItemData.data}}/>
                        :
                            treeItemData.explanations
                                ? getExplanationsLabel(treeItemData.explanations, treeItemData.data)
                                : <div dangerouslySetInnerHTML={{__html: treeItemData.data}}/>
                    }
                    children={children}
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
                disableSelection={true}
                selected={props.selected}
                onNodeToggle={props.setSelected ? handleExpandedCheckbox : handleExpanded}
                onNodeSelect={props.setSelected != undefined ? (node, ids)=>{props.setSelected(ids)} : undefined}
            >
                {props.treeItems.children!=null ? getTreeItemsFromData(props.treeItems.children) : <Typography sx={{fontWeight: 'bold'}}>No axioms to display</Typography>}
            </TreeView>
        </>
    )
}

export default OntologyTreeView;