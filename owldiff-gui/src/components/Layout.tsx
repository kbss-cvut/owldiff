import * as React from 'react'
import {navigate} from 'gatsby'
import {makeStyles} from '@mui/styles';
import {AppBar, Button, Container, Menu, MenuItem, Toolbar, Typography} from '@mui/material';

interface LayoutProps {
    pageTitle: string,
    children?: React.ReactNode;
}

interface PageProps {
    title: string,
    link: string,
}

const useStyles = makeStyles({
    menuItem: {
        color: 'white !important',
        marginRight: '24px !important',
    },
    content: {
        marginTop: 70,
        paddingLeft: 220,
        paddingRight: 220
    }
});

const Layout = (props: LayoutProps) => {
    const classes = useStyles();

    const menuPages: PageProps[] = [
        {
            title: "OWLDiff",
            link: "/"
        },
        {
            title: "About",
            link: "/about"
        }
    ]

    return (
        <div>
            <title>{props.pageTitle}</title>
            <AppBar position="fixed" sx={{zIndex: (theme) => theme.zIndex.drawer + 1}}>
                <Container maxWidth="xl">
                    <Toolbar disableGutters>
                        {menuPages.map((page) => (
                            <Button className={classes.menuItem} variant="text" size="large" key={page.title}
                                    onClick={() => navigate(page.link)}>
                                {page.title}
                            </Button>
                        ))}
                    </Toolbar>
                </Container>
            </AppBar>
            <main className={classes.content}>
                {props.children}
            </main>
        </div>
    )
}
export default Layout