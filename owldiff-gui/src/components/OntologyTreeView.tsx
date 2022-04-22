import * as React from 'react';
import {ColorsSettings, NodeModelDto} from "../api/ontologyApi";
import TreeItem from "@mui/lab/TreeItem";
import TreeView from "@mui/lab/TreeView";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import AutoSizer from "react-virtualized-auto-sizer";
import { VariableSizeList as List, areEqual, FixedSizeListProps } from "react-window";
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

const getTreeItemsFromDataVirtualized = (treeItems: NodeModelDto[], usedIds, props, getCheckboxLabel, getExplanationsLabel, layer: number, index: number, paddings, setPaddings) => {
    const currentRef = React.useRef();
    const innerRef = React.useRef();

    const handleThings = (heigth: number) => {
        //console.log(layer, heigth)
        const tmp = paddings.find(pad => pad.curLayer == layer && pad.curIndex == index)
        const tmpArr = [...paddings]
        if(tmp != null){
            tmpArr.find(pad => pad.curLayer == layer && pad.curIndex == index).curHeight = heigth
            setPaddings(tmpArr)
        }else{
            tmpArr.push({curLayer: layer, curIndex: index, curHeight: heigth})
            setPaddings(tmpArr)
        }
        setPaddings(tmpArr)
    }

    React.useEffect(() => {
        if(currentRef.current && innerRef.current){
            // @ts-ignore
            //console.log(currentRef.current)
            if(currentRef.current?.clientHeight < innerRef.current?.clientHeight){
                // @ts-ignore
                handleThings(currentRef.current?.clientHeight)
            }else{
                // @ts-ignore
                handleThings(innerRef.current?.clientHeight)
            }

        }else{
            //console.log(currentRef.current)
            handleThings(0)
        }
    },[currentRef.current])

/*
    const measuredRef = React.useCallback(node => {
        if (node !== null) {
            //console.log(node.getBoundingClientRect().height);
            handleThings(node.getBoundingClientRect().height)
        }else{
            //console.log(node)
            handleThings(0)
        }
    }, []);
*/
    return  <List
        height={700-layer*100}
        width={'100%'}
        outerRef={currentRef}
        innerRef={innerRef}
        //style={layer==1 && {overflow: 'hidden'}}
        itemData={{
            items: treeItems,
            usedIds: usedIds,
            getTreeItemsFromData: getTreeItemsFromDataVirtualized,
            props: props,
            getCheckboxLabel: getCheckboxLabel,
            getExplanationsLabel: getExplanationsLabel,
            layer: layer,
            paddings: paddings,
            setPaddings: setPaddings
        }}
        itemCount={treeItems.length}
        estimatedItemSize={100}
        itemSize={(index) =>  (treeItems[index].data.length + 40) / ( window.innerWidth / 800 )}
        useIsScrolling={true}
    >
        {treeItemRenderVirtualized}
    </List>
};

const treeItemRender = (treeItems: NodeModelDto[], usedIds, props, getCheckboxLabel, getExplanationsLabel, layer: number, index: number, paddings, setPaddings) => {
    return treeItems.map(treeItemData => {
        if(usedIds.includes(treeItemData.id)){
            return;
        }
        let children = undefined;
        if (treeItemData.children && treeItemData.children.length > 0) {
            usedIds.push(treeItemData.id);
            if(treeItemData.children.length < 50){
                children = treeItemRender(treeItemData.children, usedIds, props, getCheckboxLabel, getExplanationsLabel, layer + 1, index, paddings, setPaddings);
            }else{
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

// @ts-ignore
const treeItemRenderVirtualized = React.memo( ({ data, index, style }) => {
    const { items, usedIds, getTreeItemsFromData, props, getCheckboxLabel, getExplanationsLabel, layer, paddings, setPaddings } = data;
    const treeItemData = items[index]
    if(usedIds.includes(treeItemData.id)){
        return <></>;
    }
    let children = undefined;
    if (treeItemData.children && treeItemData.children.length > 0) {
        usedIds.push(treeItemData.id);
        children = getTreeItemsFromData(treeItemData.children, usedIds, props, getCheckboxLabel, getExplanationsLabel, layer + 1, index, paddings, setPaddings)
    }

    const currentPadding = React.useRef(0);
    const handleThings = () => {
        //console.log("layer" + layer, "index" + index, paddings)
        let total = 0
        paddings.forEach(pad => {
            if(pad.curLayer>layer && pad.curIndex<index){
                total = total + pad.curHeight
            }
        })
        currentPadding.current = total
    }
    React.useEffect(() => {
        handleThings()
    },[paddings])
    return (
        <TreeItem
            key={treeItemData.id}
            style={{...style, top: `${parseFloat(style.top) + currentPadding.current}px`}}
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
                props.setSelected ?
                    (treeItemData.isAxiom == true && treeItemData.common == false)
                        ? getCheckboxLabel(treeItemData)
                        : <div dangerouslySetInnerHTML={{__html: treeItemData.data}}/>
                    :
                    treeItemData.explanations
                        ? getExplanationsLabel(treeItemData.explanations, treeItemData.data)
                        : <div dangerouslySetInnerHTML={{__html: treeItemData.data}}/>
            }
        >
            {children}
        </TreeItem>
    );

}, areEqual);

const OntologyTreeView = (props: OntologyTreeViewProps) => {


    const [paddings, setPaddings] = React.useState([])
    let usedIds: number[] = [];
    let allPossibleAxioms: string[] = [];

    const searchAxiom = (node: NodeModelDto) => {
        if(node.isAxiom && !node.common){
            allPossibleAxioms.push(node.id.toString())
        }
        if(node.children && node.children.length > 0){
            node.children.forEach((child) => searchAxiom(child))
        }
    }
    searchAxiom(props.treeItems);
    const [selectedAll, setSelectedAll] = React.useState<boolean>(props.selected ? allPossibleAxioms.length==props.selected.length : false);

    React.useEffect(()=>{
        if(props.setSelected){
            if(selectedAll==true){
                props.setSelected(allPossibleAxioms)
            }else{
                props.setSelected([])
            }
        }
    },[selectedAll])

    const handleExpanded = (event, nodes: string[]) =>{
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
        return(
            <FormControlLabel
                sx={{ alignItems: 'flex-start' }}
                control={
                    <Checkbox
                        sx={{ paddingTop: 0}}
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
        return(
            <Tooltip followCursor title={<h2>{explanation}</h2>}><div dangerouslySetInnerHTML={{__html: data}}/></Tooltip>
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
                defaultCollapseIcon={<ExpandLessIcon />}
                defaultExpandIcon={<ExpandMoreIcon />}
                expanded={props.expanded}
                disableSelection={true}
                selected={props.selected}
                onNodeToggle={props.setSelected ? handleExpandedCheckbox : handleExpanded}
                onNodeSelect={props.setSelected ? (e, ids)=>{props.setSelected(ids)} : undefined}
            >
                {props.treeItems.children ? treeItemRender(props.treeItems.children, usedIds, props, getCheckboxLabel, getExplanationsLabel, 1, 0, paddings, setPaddings) : <Typography sx={{fontWeight: 'bold'}}>No axioms to display</Typography>}
            </TreeView>
        </>
    )
}

export default OntologyTreeView;