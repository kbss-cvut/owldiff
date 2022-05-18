import * as React from 'react';
import {ColorsSettings, NodeModelDto} from "../../api/ontologyApi";
import TreeView from "@mui/lab/TreeView";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
// @ts-ignore
import * as styles from '../Components.module.css';
import {Checkbox, Tooltip, FormControlLabel, Typography} from "@mui/material";
import {treeItemRender} from "./OntologyTreeItem";

interface OntologyTreeViewProps {
    treeItems: NodeModelDto,
    colorSettings?: ColorsSettings,
    expanded: string[],
    setExpanded: (value: string[]) => void;
    selected?: string[],
    setSelected?: (value: string[]) => void;
}

const OntologyTreeView = (props: OntologyTreeViewProps) => {


    const [paddings, setPaddings] = React.useState([])
    let usedIds: number[] = [];
    let allPossibleAxioms: string[] = [];

    const searchAxiom = (node: NodeModelDto) => {
        if (node.isAxiom && !node.common) {
            allPossibleAxioms.push(node.id.toString())
        }
        if (node.children && node.children.length > 0) {
            node.children.forEach((child) => searchAxiom(child))
        }
    }
    searchAxiom(props.treeItems);
    const [selectedAll, setSelectedAll] = React.useState<boolean>(props.selected ? allPossibleAxioms.length == props.selected.length : false);

    React.useEffect(() => {
        if (props.setSelected) {
            if (selectedAll == true) {
                props.setSelected(allPossibleAxioms)
            } else {
                props.setSelected([])
            }
        }
    }, [selectedAll])

    const handleExpanded = (event, nodes: string[]) => {
        props.setExpanded(nodes);
    }

    const handleExpandedCheckbox = (event, nodes: string[]) => {
        if (event.target.closest(".MuiTreeItem-iconContainer")) {
            props.setExpanded(nodes);
        }
    }

    const getOnSelected = (checked: boolean, treeItem: NodeModelDto) => {
        let array = checked
            ? [...props.selected, treeItem.id.toString()]
            : props.selected.filter((value) => value != treeItem.id.toString());

        props.setSelected(array);
    }

    const getCheckboxLabel = (treeItem: NodeModelDto) => {
        return (
            <FormControlLabel
                sx={{alignItems: 'flex-start'}}
                control={
                    <Checkbox
                        sx={{paddingTop: 0}}
                        checked={props.selected.some((item) => item === treeItem.id.toString())}
                        onChange={(event) =>
                            getOnSelected(event.target.checked, treeItem)
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
        return (
            <Tooltip followCursor title={<h2>{explanation}</h2>}>
                <div dangerouslySetInnerHTML={{__html: data}}/>
            </Tooltip>
        )
    }


    return (
        <>
            <div>{props.treeItems.data}</div>
            {(props.setSelected && props.treeItems.children) &&
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={selectedAll}
                            onChange={(event) => {
                                setSelectedAll(event.target.checked);
                            }}
                            onClick={(e) => e.stopPropagation()}
                        />
                    }
                    label={"Select all"}
                />}
            <TreeView
                defaultCollapseIcon={<ExpandLessIcon/>}
                defaultExpandIcon={<ExpandMoreIcon/>}
                expanded={props.expanded}
                disableSelection={true}
                selected={props.selected}
                onNodeToggle={props.setSelected ? handleExpandedCheckbox : handleExpanded}
                onNodeSelect={props.setSelected ? (e, ids) => {
                    props.setSelected(ids)
                } : undefined}
            >
                {props.treeItems.children ? treeItemRender(props.treeItems.children, usedIds, props, getCheckboxLabel, getExplanationsLabel, 1, 0, paddings, setPaddings) :
                    <Typography sx={{fontWeight: 'bold'}}>No axioms to display</Typography>}
            </TreeView>
        </>
    )
}

export default OntologyTreeView;