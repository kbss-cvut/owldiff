import APIKit from "./Base";

export interface ComparisonDto{
    original: OntologyDataDto,
    update: OntologyDataDto,
    sessionId: string,
    sessionTimer: number
}

export interface OntologyDataDto{
    ontologyName: string,
    data: NodeModelDto
}

export interface NodeModelDto{
    data: string,
    id: number,
    explanations: string,
    common: boolean,
    inferred: boolean,
    isAxiom: boolean,
    useCex: boolean,
    children: NodeModelDto[]
}

export interface ComparisonSettings{
    diffType: 'SYNTACTIC'|'ENTAILMENT'|'CEX',
    diffView: 'LIST_VIEW'|'SIMPLE_FRAME_VIEW'|'CLASSIFIED_FRAME_VIEW',
    syntax: 'MANCHESTER'|'DL',
    generateExplanation: boolean,
    showCommon: boolean,
    sid?: string,
    colors?: ColorsSettings
}

export interface ColorsSettings{
    common: string,
    inferred: string,
    cex: string
}

export enum OWLDocumentFormats{
    OBO = "OBO",
    OWL = "OWL",
    TTL = "TTL",
    OWX = "OWX",
    OMN = "OMN",
    OFN = "OFN"
}

export const uploadOntologies = (original: File, update: File, { diffType, diffView, syntax, generateExplanation, sid, showCommon} : ComparisonSettings) => {
    let formData = new FormData();
    formData.append("originalFile",original);
    formData.append("updateFile",update);
    formData.append("diffType",diffType);
    formData.append("diffView",diffView);
    formData.append("syntax",syntax);
    if(sid) formData.append("sid",sid);
    formData.append("generateExplanation",generateExplanation.toString())
    formData.append("showCommon",showCommon.toString())
    return APIKit.post("/ontology/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        }}
    )
    .then<ComparisonDto>(response => {
        return response.data;
    }).catch(error => {
        throw(error);
    })
}

export const getComparisonResult = (sid: string) => {
    return APIKit.get("/ontology/upload/" + sid).then<ComparisonDto>(response => {
        return response.data;
    }).catch(error => {
        throw(error);
    })
}

export const mergeOntologies = (sid: string, add: string[], remove: string[], format: OWLDocumentFormats, filename?: string) => {
    let formData = new FormData();
    if(filename) formData.append("filename",filename);
    add.forEach(a => {
        formData.append("add",a);
    })
    remove.forEach(r => {
        formData.append("remove",r);
    })
    formData.append("sid",sid);
    formData.append("format",format.toString());
    return APIKit.post("/ontology/merge", formData, {
        headers: {
            "Content-Type": "multipart/form-data",
        }}
    ).then(response => {
        return response;
    }).catch(error => {
        throw(error);
    })
}