package edu.cpp.admissions.bean;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "status_report")
public class StatusAuditReport implements Serializable {


    private static final long serialVersionUID = -2686427795965246196L;

    @Id
    @Column(name="sr")
    @GeneratedValue(strategy=GenerationType.IDENTITY, generator = "SR_SEQ")
    @SequenceGenerator(name = "SR_SEQ", sequenceName = "SR_SEQ", allocationSize = 1)
    private Long sr;


    @Column(name="bronco_id")
    private String broncoId;

    @Column(name="status")
    private String status;

    @Column(name="processor")
    private String processor;

    @Column(name="wam")
    private String wam;

    public String getWam() {
        return wam;
    }

    public void setWam(String wam) {
        this.wam = wam;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getSr() {
        return sr;
    }

    public void setSr(Long sr) {
        this.sr = sr;
    }

    public String getBroncoId() {
        return broncoId;
    }

    public void setBroncoId(String broncoId) {
        this.broncoId = broncoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }
}
