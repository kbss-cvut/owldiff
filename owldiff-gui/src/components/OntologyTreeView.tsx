import * as React from 'react';
import {ColorsSettings, NodeModelDto} from "../api/ontologyApi";
import TreeItem from "@mui/lab/TreeItem";
import TreeView from "@mui/lab/TreeView";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// @ts-ignore
import * as styles from './Components.module.css';
import {Checkbox, Tooltip, FormControlLabel} from "@mui/material";

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

    const getChildById = (treeItemParent: NodeModelDto, id: string) => {
        let array: string[] = [];

        const getAllChild = (treeItem: NodeModelDto | null) => {
            if (treeItem === null) return [];
            array.push(treeItem.id.toString());
            if (Array.isArray(treeItem.children)) {
                treeItem.children.forEach((node) => {
                    array = [...array, ...getAllChild(node)];
                    array = array.filter((v, i) => array.indexOf(v) === i);
                });
            }
            return array;
        }

        const getNodeById = (treeItem: NodeModelDto, id: string) => {
            if (treeItem.id.toString() === id) {
                return treeItem;
            } else if (Array.isArray(treeItem.children)) {
                let result = null;
                treeItem.children.forEach((node) => {
                    if (!!getNodeById(node, id)) {
                        result = getNodeById(node, id);
                    }
                });
                return result;
            }

            return null;
        }

        return getAllChild(getNodeById(treeItemParent, id));
    }

    const getOnChange = (checked: boolean, treeItem: NodeModelDto) => {
        const allNode: string[] = getChildById(props.treeItems, treeItem.id.toString());
        let array = checked
            ? [...props.selected, ...allNode]
            : props.selected.filter((value) => !allNode.includes(value));

        props.setSelected(array);
    }

    const getCheckboxLabel = (treeItem: NodeModelDto) => {
        return(
            <FormControlLabel
                control={
                    <Checkbox
                        checked={props.selected.some((item) => item === treeItem.id.toString())}
                        onChange={(event) =>
                            getOnChange(event.currentTarget.checked, treeItem)
                        }
                        //onClick={(e) => e.stopPropagation()}
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
                        treeItemData.common ? {color: props.colorSettings.common} :
                        treeItemData.inferred ? {color: props.colorSettings.inferred} :
                        treeItemData.useCex ? {color: props.colorSettings.cex} :
                        {color: 'green'}
                        : undefined}
                    nodeId={treeItemData.id.toString()}
                    classes={{content: styles.ontology_tree_view_item,
                              selected: styles.ontology_tree_view_item_selected}}
                    label={
                        props.setSelected != null ?
                            getCheckboxLabel(treeItemData)
                            :
                        treeItemData.explanations ?
                        getExplanationsLabel(treeItemData.explanations, treeItemData.data)
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
                multiSelect={props.setSelected != null}
                onNodeToggle={props.setSelected ? handleExpandedCheckbox : handleExpanded}
                onNodeSelect={props.setSelected != null ? (node, ids)=>{props.setSelected(ids)} : undefined}
            >
                {getTreeItemsFromData(props.treeItems.children)}
            </TreeView>
        </>
    )
}

export default OntologyTreeView;