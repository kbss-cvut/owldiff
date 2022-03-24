import {
    Box,
    Collapse,
    Divider,
    Drawer,
    IconButton,
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    Toolbar,
    Typography
} from "@mui/material";
import ChevronRightIcon from '@mui/icons-material/ChevronRight';
import ChevronLeftIcon from '@mui/icons-material/ChevronLeft';
// @ts-ignore
import React, {useEffect, useState} from "react"
import {ComparisonSettings} from "../api/ontologyApi";
import {ExpandLess, ExpandMore} from "@mui/icons-material";
// @ts-ignore
import * as styles from './Components.module.css';


export interface ToolkitProps{
    comparisonSettings: ComparisonSettings,
    setComparisonSettings: (value: ComparisonSettings) => void,
    onMerge: () => void
}

interface settingType{
    text: string,
    children?: settingType[],
    onClick?: () => void,
    selected?: boolean
}

const Toolkit = (props: ToolkitProps) => {
    const [open, setOpen] = React.useState<boolean>(false);

    useEffect(()=>{
       setOpen(localStorage.getItem('toolOpen')==="true");
    },[])

    const settings : settingType[] = [
        {
            text: props.comparisonSettings.showCommon==false ? 'Show common' : 'Hide common',
            onClick: () => {
                props.setComparisonSettings({...props.comparisonSettings, showCommon: !props.comparisonSettings.showCommon});
            },
            selected: props.comparisonSettings.showCommon==true
        },
        {
            text: props.comparisonSettings.generateExplanation==false ? 'Show explanations' : 'Hide explanations',
            onClick: () => {
                props.setComparisonSettings({...props.comparisonSettings, diffType: "ENTAILMENT", generateExplanation: !props.comparisonSettings.generateExplanation});
            },
            selected: props.comparisonSettings.generateExplanation==true
        },
        {
            text: props.comparisonSettings.diffType!="CEX" ? 'Use CEX' : 'Dont use CEX',
            onClick: () => {
                props.setComparisonSettings({...props.comparisonSettings, diffType: props.comparisonSettings.diffType!="CEX" ? 'CEX' : 'SYNTACTIC',});
            },
            selected: props.comparisonSettings.diffType=="CEX"
        },
        {
            text: 'Change syntax',
            children: [
                {
                    text: 'Manchester',
                    onClick: () => {
                        props.setComparisonSettings({...props.comparisonSettings, syntax: "MANCHESTER"});
                    },
                    selected: props.comparisonSettings.syntax=="MANCHESTER"
                },
                {
                    text: 'DL',
                    onClick: () => {
                        props.setComparisonSettings({...props.comparisonSettings, syntax: "DL"});
                    },
                    selected: props.comparisonSettings.syntax=="DL"
                },
            ]
        },
        {
            text: 'Change view',
            children: [
                {
                    text: 'List view',
                    onClick: () => {
                        props.setComparisonSettings({...props.comparisonSettings, diffView: "LIST_VIEW"});
                    },
                    selected: props.comparisonSettings.diffView=="LIST_VIEW"
                },
                {
                    text: 'Classified frame view',
                    onClick: () => {
                        props.setComparisonSettings({...props.comparisonSettings, diffView: "CLASSIFIED_FRAME_VIEW"});
                    },
                    selected: props.comparisonSettings.diffView=="CLASSIFIED_FRAME_VIEW"
                },
                {
                    text: 'Simple frame view',
                    onClick: () => {
                        props.setComparisonSettings({...props.comparisonSettings, diffView: "SIMPLE_FRAME_VIEW"});
                    },
                    selected: props.comparisonSettings.diffView=="SIMPLE_FRAME_VIEW"
                }
            ]
        },
    ]

    const settings2 : settingType[] = [
        {
            text: 'Merge ontologies',
            onClick: () => {
                props.onMerge();
            },
        }
    ]

    const handleDrawerOpen = () => {
        setOpen(true);
        localStorage.setItem('toolOpen', "true")  
    };

    const handleDrawerClose = () => {
        setOpen(false);
        localStorage.setItem('toolOpen', "false")  
    };

    const renderSettingChildren = (parent: settingType) => {
        const [open, setOpen] = useState<boolean>(false);
        const handleClick = () => {
            setOpen(!open);
        };

        return(
            <div key={parent.text}>
                <ListItem button key={parent.text} onClick={handleClick}>
                    <ListItemText primary={parent.text}/>
                    {open ? <ExpandLess /> : <ExpandMore />}
                </ListItem>
                <Collapse in={open} timeout="auto" unmountOnExit>
                    <List component="div" disablePadding>
                    {parent.children && parent.children.map(child => {
                        return(
                            <ListItemButton sx={{ pl: 4 }} key={child.text} onClick={child.onClick} selected={child.selected} classes={{selected: styles.button_selected}}>
                                <ListItemText primary={child.text}/>
                            </ListItemButton>
                        )
                    })}
                    </List>
                </Collapse>
            </div>
        )
    }

    return (
        <div className={styles.toolkit_button}>
            <IconButton
                color="inherit"
                aria-label="open drawer"
                edge="end"
                onClick={handleDrawerOpen}
                sx={{ ...(open && { display: 'none' }), position: "absolute", left:0, top: 60 }}
            >
                <Typography>Show Tools</Typography><ChevronRightIcon />
            </IconButton>
            <Drawer
            anchor="left"
            open={open}
            sx={{
                width: 200,
                flexShrink: 0,
                [`& .MuiDrawer-paper`]: { width: 200, boxSizing: 'border-box' },
            }}
            variant="persistent"
            >
                <Toolbar />
                <Box sx={{ overflow: 'auto' }}>
                <IconButton onClick={handleDrawerClose}>
                    <ChevronLeftIcon />
                </IconButton>
                <List>
                    {settings.map((setting, index) => (
                        setting.children ? renderSettingChildren(setting) :
                        <ListItemButton key={setting.text} onClick={setting.onClick} selected={setting.selected} classes={{selected: styles.button_selected}}>
                            <ListItemText primary={setting.text}/>
                        </ListItemButton>
                    ))}
                </List>
                <Divider />
                <List>
                    {settings2.map((setting, index) => (
                        setting.children ? renderSettingChildren(setting) :
                            <ListItemButton key={setting.text} onClick={setting.onClick} selected={setting.selected} classes={{selected: styles.button_selected}}>
                                <ListItemText primary={setting.text}/>
                            </ListItemButton>
                    ))}
                </List>
                </Box>
            </Drawer>
        </div>
    )
}

export default Toolkit