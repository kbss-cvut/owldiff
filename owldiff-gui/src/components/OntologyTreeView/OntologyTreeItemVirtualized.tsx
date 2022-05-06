// @ts-ignore
import * as React from "react";
import TreeItem from "@mui/lab/TreeItem";
import {NodeModelDto} from "../../api/ontologyApi";
// @ts-ignore
import * as styles from '../Components.module.css';
import { VariableSizeList as List, areEqual } from "react-window";

// @ts-ignore
export const treeItemRenderVirtualized = React.memo( ({ data, index, style }) => {
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

export const getTreeItemsFromDataVirtualized = (treeItems: NodeModelDto[], usedIds, props, getCheckboxLabel, getExplanationsLabel, layer: number, index: number, paddings, setPaddings) => {
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