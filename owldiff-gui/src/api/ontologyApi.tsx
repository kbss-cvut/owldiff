import APIKit from "./Base";

export interface ComparisonDto{
    original: OntologyDataDto,
    update: OntologyDataDto,
    sessionId: string,
    sessionTimer: string
}

export interface OntologyDataDto{
    name: string,
    data: NodeModelDto
}

export interface NodeModelDto{
    data: string,
    id: number,
    explanations: string,
    isCommon: boolean,
    isInferred: boolean,
    useCex: boolean,
    children: NodeModelDto[]
}

export interface ComparisonSettings{
    diffType: 'SYNTACTIC'|'ENTAILMENT'|'CEX',
    diffView: 'LIST_VIEW'|'SIMPLE_FRAME_VIEW'|'CLASSIFIED_FRAME_VIEW',
    syntax: 'MANCHESTER'|'DL',
    generateExplanation: boolean,
    sid?: string,
    colors?: ColorsSettings
}

export interface ColorsSettings{
    common: string,
    inferred: string,
    cex: string
}

export enum OWLDocumentFormats{
    OBO,
    OWL,
    TTL,
    OWX,
    OMN,
    OFN
}

export const uploadOntologies = (original: File, update: File, { diffType, diffView, syntax, generateExplanation, sid} : ComparisonSettings) => {
    let formData = new FormData();
    formData.append("originalFile",original);
    formData.append("updateFile",update);
    formData.append("diffType",diffType);
    formData.append("diffView",diffView);
    formData.append("syntax",syntax);
    if(sid) formData.append("sid",sid);
    formData.append("generateExplanation",generateExplanation.toString())
    return APIKit.post<ComparisonDto>("/ontology/upload", formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        }}
    )
    .then(response => {
        return response.data;
    }).catch(error => {
        throw(error);
    })
}

export const mergeOntologies = (sid: string, filename: string, add: string[], remove: string[], format: OWLDocumentFormats) => {
    let formData = new FormData();
    formData.append("filename",filename);
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
        return response.data;
    }).catch(error => {
        throw(error);
    })
}