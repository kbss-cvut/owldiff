import {NodeModelDto} from "../../api/ontologyApi";
import TreeItem from "@mui/lab/TreeItem";
import * as React from "react";
import {getTreeItemsFromDataVirtualized} from "./OntologyTreeItemVirtualized";
// @ts-ignore
import * as styles from '../Components.module.css';

export const treeItemRender = (treeItems: NodeModelDto[], usedIds, props, getCheckboxLabel, getExplanationsLabel, layer: number, index: number, paddings, setPaddings) => {
    return treeItems.map(treeItemData => {
        if (usedIds.includes(treeItemData.id)) {
            return;
        }
        let children = undefined;
        if (treeItemData.children && treeItemData.children.length > 0) {
            usedIds.push(treeItemData.id);
            if (treeItemData.children.length < 50) {
                children = treeItemRender(treeItemData.children, usedIds, props, getCheckboxLabel, getExplanationsLabel, layer + 1, index, paddings, setPaddings);
            } else {
                children = getTreeItemsFromDataVirtualized(treeItemData.children, usedIds, props, getCheckboxLabel, getExplanationsLabel, layer + 1, index, paddings, setPaddings)
            }
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
                classes={{
                    content: styles.ontology_tree_view_item,
                    selected: styles.ontology_tree_view_item_selected
                }}
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